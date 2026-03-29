package com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb;

import com.echocano.zestorder.product.application.exception.RepositoryException;
import com.echocano.zestorder.product.application.port.output.ProductRepositoryOutputPort;
import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.ProductDocument;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.mapper.ProductMongoMapper;
import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.repository.ProductMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMongoRepositoryOutputAdapter implements ProductRepositoryOutputPort {

    private final ProductMongoRepository repository;
    private final ProductMongoMapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;

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
    public Mono<CorePage<Product>> findAllPaged(String status, String search, int page, int size
            , String sort, String direction) {
        Criteria criteria = new Criteria();
        if (status != null && !status.isBlank()) criteria.and("status").is(status);
        if (search != null && !search.isBlank()) criteria.and("name").regex(search, "i");
        return findByCriteria(criteria, page, size, sort, direction);
    }

    @Override
    public Mono<CorePage<Product>> findByCategory(String categoryId, String status, int page, int size, String sort, String direction) {
        Criteria criteria = Criteria.where("category.name").is(categoryId);
        if (status != null && !status.isBlank()) {
            criteria.and("status").is(status);
        }
        return findByCriteria(criteria, page, size, sort, direction);
    }

    private Mono<CorePage<Product>> findByCriteria(Criteria criteria, int page, int size,  String sort, String direction) {
        Query query = new Query(criteria)
                .with(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort)));
        return Mono.zip(
                mongoTemplate.find(query, ProductDocument.class).collectList(),
                mongoTemplate.count(Query.query(criteria), ProductDocument.class)
        ).map(tuple -> {
            List<Product> products = mapper.toDomains(tuple.getT1());
            long totalElements = tuple.getT2();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            boolean isLast = page >= totalPages - 1;
            return new CorePage<>(
                    products,
                    page,
                    size,
                    totalElements,
                    totalPages,
                    isLast
            );
        });
    }

    @Override
    public Mono<Product> findByNameAndCategory(String name, String categoryName) {
        return repository
                .findByNameAndCategoryName(name, categoryName)
                .map(mapper::toDomain);
    }
}
