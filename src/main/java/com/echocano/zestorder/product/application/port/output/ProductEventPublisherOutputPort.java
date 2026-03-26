package com.echocano.zestorder.product.application.port.output;

import com.echocano.zestorder.product.domain.Product;
import reactor.core.publisher.Mono;

public interface ProductEventPublisherOutputPort {

    Mono<Void> publish(Product product);
}
