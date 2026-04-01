package com.echocano.zestorder.product.application.service;

import com.echocano.zestorder.product.application.exception.ProductAlreadyExistsException;
import com.echocano.zestorder.product.application.exception.ProductNotFoundException;
import com.echocano.zestorder.product.application.exception.ProductWasDeletedException;
import com.echocano.zestorder.product.application.port.input.CreateProductInputPort;
import com.echocano.zestorder.product.application.port.input.DeleteProductInputPort;
import com.echocano.zestorder.product.application.port.input.FindProductInputPort;
import com.echocano.zestorder.product.application.port.input.UpdateProductInputPort;
import com.echocano.zestorder.product.application.port.output.ProductEventPublisherOutputPort;
import com.echocano.zestorder.product.application.port.output.ProductRepositoryOutputPort;
import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.domain.ProductStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService implements CreateProductInputPort, FindProductInputPort, DeleteProductInputPort, UpdateProductInputPort {

    private static final String OUTCOME_TAG = "outcome";
    private static final String CATEGORY_TAG = "category";
    private static final String TYPE_TAG = "type";
    private static final String FIND_LATENCY_TAG = "zestorder.product.find";
    private static final String CREATE_LATENCY_TAG = "zestorder.product.create";
    private static final String UPDATE_LATENCY_TAG = "zestorder.product.update";
    private static final String DELETE_LATENCY_TAG = "zestorder.product.delete";
    private final ProductRepositoryOutputPort repository;
    private final MeterRegistry meterRegistry;
    private final ProductEventPublisherOutputPort eventPublisher;
    private final ObservationRegistry observationRegistry;

    @Override
    public Mono<Product> execute(Product product) {
        return repository.findByNameAndCategory(product.getName(), product.getCategory().name())
                .flatMap(existing -> {
                    if (ProductStatus.DELETED.equals(existing.getStatus())) {
                        return Mono.<Product>error(new ProductWasDeletedException(
                                String.format("Product '%s' in category '%s' was previously deleted.",
                                        product.getName(), product.getCategory().name())));
                    }
                    return Mono.error(new ProductAlreadyExistsException(
                            String.format("Product with name %s and Category %s already exists.",
                                    product.getName(), product.getCategory().name())));
                })
                .switchIfEmpty(Mono.defer(() -> repository
                        .save(product.toBuilder()
                                .status(ProductStatus.ACTIVE)
                                .createdAt(Instant.now())
                                .build())
                        .flatMap(savedProduct ->
                                eventPublisher
                                        .publish(savedProduct)
                                        .retry(2)
                                        .onErrorResume(e -> {
                                            log.error("Failed to publish event for product {}", savedProduct.getId(), e);
                                            return Mono.empty();
                                        })
                                        .then(Mono.just(savedProduct))
                        )))
                .doOnSuccess(productCreated -> {
                    if (productCreated != null) {
                        log.info("Product {} created with ID {}", productCreated.getName(), productCreated.getId());
                    }
                })
                .name(CREATE_LATENCY_TAG)
                .tag(CATEGORY_TAG, product.getCategory().name())
                .tap(Micrometer.observation(observationRegistry));
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(
                        String.format("Product with ID [%s] not found", id))))
                .flatMap(product ->
                        repository.save(product.toBuilder()
                                .status(ProductStatus.DELETED)
                                .updatedAt(Instant.now())
                                .build()))
                .doOnSuccess(product -> log.info("AUDIT: Product [{}] marked as DELETED.", product.getSku()))
                .doOnError(e -> log.error("Failed to delete product ID {}: {}", id, e.getMessage()))
                .name(DELETE_LATENCY_TAG)
                .tag(CATEGORY_TAG, "all")
                .tap(Micrometer.observation(observationRegistry))
                .then();
    }

    @Override
    public Flux<Product> findAllActive() {
        return null;
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id)
                .name(FIND_LATENCY_TAG)
                .tag(TYPE_TAG, "id")
                .tap(Micrometer.observation(observationRegistry));
    }

    @Override
    public Mono<CorePage<Product>> findByCategoryPaged(String categoryName, int page, int size, String sort, String direction) {
        return repository
                .findByCategory(categoryName, ProductStatus.ACTIVE.name(), page, size, sort, direction)
                .doOnSuccess(products -> log.info("Total products {} found with category {}", products.getContent().size(), categoryName))
                .name(FIND_LATENCY_TAG)
                .tag(TYPE_TAG, CATEGORY_TAG)
                .tag(CATEGORY_TAG, categoryName)
                .tap(Micrometer.observation(observationRegistry));
    }

    @Override
    public Mono<CorePage<Product>> findActivePaged(String search, int page, int size, String sort, String direction) {
        String query = (search == null) ? "" : search;
        return repository
                .findAllPaged(ProductStatus.ACTIVE.name(), query, page, size, sort, direction)
                .name(FIND_LATENCY_TAG)
                .tag("search_active", query)
                .tap(Micrometer.observation(observationRegistry));
    }

    @Override
    public Mono<Product> update(String id, Product product) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(existingProduct -> {
                    if (!existingProduct.getName().equalsIgnoreCase(product.getName())) {
                        return repository.findByNameAndCategory(product.getName(), product.getCategory().name())
                                .flatMap(collision -> Mono.<Product>error(
                                        new ProductAlreadyExistsException(
                                                String.format("Product with name %s and Category %s exists."
                                                        , product.getName(), product.getCategory().name()))))
                                .switchIfEmpty(Mono.just(existingProduct));
                    }
                    return Mono.just(existingProduct);
                })
                .flatMap(existing -> {
                    Product updatedProduct = existing.toBuilder()
                            .name(product.getName())
                            .basePrice(product.getBasePrice())
                            .category(product.getCategory())
                            .updatedAt(Instant.now())
                            .build();
                    return repository.save(updatedProduct);
                })
                .name(UPDATE_LATENCY_TAG)
                .tag(CATEGORY_TAG, product.getCategory().name())
                .tap(Micrometer.observation(observationRegistry));
    }
}
