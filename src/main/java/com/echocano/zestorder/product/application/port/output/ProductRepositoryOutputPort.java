package com.echocano.zestorder.product.application.port.output;

import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryOutputPort {

    Mono<Product> save(Product product);
    Mono<Product> findBySku(String sku);
    Mono<Product> findById(String id);
    Flux<Product> findAll();
    Mono<CorePage<Product>> findAllPaged(String status, String search, int page, int size, String sort, String direction);
    Mono<CorePage<Product>> findByCategory(String categoryId, String status, int page, int size, String sort, String direction);
    Mono<Product> findByNameAndCategory(String name, String category);
}
