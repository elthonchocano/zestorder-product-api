package com.echocano.zestorder.product.application.port.input;

import com.echocano.zestorder.product.domain.Product;
import reactor.core.publisher.Mono;

public interface CreateProductInputPort {

    Mono<Product> execute(Product product);
}
