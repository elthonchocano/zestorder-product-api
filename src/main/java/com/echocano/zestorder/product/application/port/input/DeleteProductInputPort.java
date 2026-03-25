package com.echocano.zestorder.product.application.port.input;

import reactor.core.publisher.Mono;

public interface DeleteProductInputPort {

    Mono<Void> deleteProduct(String id);
}
