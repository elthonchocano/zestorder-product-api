package com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.repository;

import com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities.ProductDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {

    Mono<ProductDocument> findBySku(String sku);

    Flux<ProductDocument> findAllByAttribute_AvailableTrue();

    @Query("{ 'id': ?0, 'status': { $ne: 'DELETED' } }")
    Mono<ProductDocument> findByIdAndNotDeleted(String id);

    @Query("{ 'status': 'ACTIVE' }")
    Flux<ProductDocument> findAllActive();

    Flux<ProductDocument> findAllByStatusAndNameContainingIgnoreCase(
            String status,
            String name,
            Pageable pageable
    );

    Mono<Long> countByStatusAndNameContainingIgnoreCase(String status, String name);

    Mono<ProductDocument> findByNameAndCategoryName(String name, String categoryName);
}
