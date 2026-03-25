package com.echocano.zestorder.product.application.port.input;

import com.echocano.zestorder.product.domain.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductInputPort {

    Mono<Product> update(String id, Product product);
}
