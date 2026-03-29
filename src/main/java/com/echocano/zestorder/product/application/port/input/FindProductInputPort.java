package com.echocano.zestorder.product.application.port.input;

import com.echocano.zestorder.product.domain.CorePage;
import com.echocano.zestorder.product.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindProductInputPort {

    Flux<Product> findAllActive();
    Mono<Product> findById(String id);
    Mono<CorePage<Product>> findByCategoryPaged(String categoryName, int page, int size, String sort, String direction);
    Mono<CorePage<Product>> findActivePaged(String search, int page, int size, String sort, String direction);
}
