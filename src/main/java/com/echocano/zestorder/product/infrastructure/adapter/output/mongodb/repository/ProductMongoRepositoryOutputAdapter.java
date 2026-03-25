package com.echocano.zestorder.product.infrastructure.adapter.output.mongodb.repository;

import com.echocano.zestorder.product.application.exception.RepositoryException;
import com.echocano.zestorder.product.application.port.output.ProductRepositoryOutputPort;
import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.output.mongodb.mapper.ProductMongoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMongoRepositoryOutputAdapter implements ProductRepositoryOutputPort {

    private final ProductMongoRepository repository;
    private final ProductMongoMapper mapper;

    @Override
    public Mono<Product> save(final Product product) {
        return repository
                .save(mapper.toDocument(product))
                .map(mapper::toDomain)
                .onErrorMap(ex -> new RepositoryException("Database error while saving: " + product.getName()))
                .switchIfEmpty(Mono.error(new RepositoryException(String.format("Unexpected empty result from database for product %s", product.getName()))));
    }

    @Override
    public Mono<Product> findBySku(String sku) {
        return repository
                .findBySku(sku)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository
                .findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findAll() {
        return repository
                .findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<CorePage<Product>> findAllPaged(String status, String search, int page, int size, String sort, String direction) {
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Mono<List<Product>> data = repository
                .findAllByStatusAndNameContainingIgnoreCase(status, search, pageable)
                .map(mapper::toDomain)
                .collectList();
        Mono<Long> count = repository
                .countByStatusAndNameContainingIgnoreCase(status, search);
        return Mono.zip(data, count)
                .map(tuple -> CorePage.<Product>builder()
                        .content(tuple.getT1())
                        .pageNumber(pageable.getPageNumber())
                        .pageSize(pageable.getPageSize())
                        .totalElements(tuple.getT2())
                        .totalPages((int) Math.ceil((double) tuple.getT2() / size))
                        .isLast(pageable.getOffset() + tuple.getT1().size() >= tuple.getT2())
                        .build());
    }

    @Override
    public Mono<Product> findByNameAndCategory(String name, String categoryName) {
        return repository
                .findByNameAndCategoryName(name, categoryName)
                .map(mapper::toDomain);
    }
}
