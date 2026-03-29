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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService implements CreateProductInputPort, FindProductInputPort, DeleteProductInputPort, UpdateProductInputPort {

    private static final String OUTCOME_TAG = "outcome";
    private final ProductRepositoryOutputPort repository;
    private final MeterRegistry meterRegistry;
    private final ProductEventPublisherOutputPort eventPublisher;

    @Override
    public Mono<Product> execute(Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
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
                .doFinally(signalType ->
                        sample.stop(Timer.builder("zestorder.product.create.latency")
                                .tag(OUTCOME_TAG, signalType.toString())
                                .tag("category", product.getCategory().name())
                                .register(meterRegistry))
                );
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(
                        String.format("Product with ID [%s] not found", id))))
                .flatMap(product ->
                        repository.save(product.toBuilder()
                                .status(ProductStatus.DELETED)
                                .updatedAt(Instant.now())
                                .build()))
                .doOnSuccess(product -> {
                    Counter.builder("zestorder.product.deleted")
                            .tag("category", product.getCategory().name())
                            .tag("status", "SUCCESS")
                            .description("Count of deleted products by category")
                            .register(meterRegistry)
                            .increment();
                    log.info("AUDIT: Product [{}] marked as DELETED.", product.getSku());
                })
                .doOnError(e -> log.error("Failed to delete product ID {}: {}", id, e.getMessage()))
                .doFinally(signalType ->
                        sample.stop(Timer.builder("zestorder.product.delete.latency")
                                .publishPercentiles(0.5, 0.95, 0.99)
                                .description("Time taken to process product deletion")
                                .tag(OUTCOME_TAG, signalType.toString())
                                .register(meterRegistry)))
                .then();
    }

    @Override
    public Flux<Product> findAllActive() {
        return null;
    }

    @Override
    public Mono<Product> findById(String id) {
        Timer.Sample sample = Timer.start(meterRegistry);
        return repository.findById(id)
                .doFinally(signalType ->
                        sample.stop(Timer.builder("zestorder.product.find.latency")
                                .serviceLevelObjectives(Duration.ofMillis(200))
                                .register(meterRegistry))
                );
    }

    @Override
    public Flux<Product> findByCategory(String categoryId) {
        return null;
    }

    @Override
    public Mono<CorePage<Product>> findActivePaged(String search, int page, int size, String sort, String direction) {
        Timer.Sample sample = Timer.start(meterRegistry);
        String query = (search == null) ? "" : search;
        return repository
                .findAllPaged(ProductStatus.ACTIVE.name(), query, page, size, sort, direction)
                .doFinally(signalType ->
                        sample.stop(Timer.builder("zestorder.product.find.latency")
                                .tag("search_active", String.valueOf(!query.isEmpty()))
                                .serviceLevelObjectives(Duration.ofMillis(200)) // SLA 200ms
                                .register(meterRegistry))
                );
    }

    @Override
    public Mono<Product> update(String id, Product product) {
        Timer.Sample sample = Timer.start(meterRegistry);
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
                .doFinally(signalType ->
                        sample.stop(Timer.builder("zestorder.product.update.latency")
                                .tag(OUTCOME_TAG, signalType.toString())
                                .register(meterRegistry)));
    }
}
