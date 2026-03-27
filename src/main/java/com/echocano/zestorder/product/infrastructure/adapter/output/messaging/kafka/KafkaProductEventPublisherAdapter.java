package com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka;

import com.echocano.zestorder.product.application.port.output.ProductEventPublisherOutputPort;
import com.echocano.zestorder.product.domain.Product;
import com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka.config.StreamBridgeWrapper;
import com.echocano.zestorder.product.infrastructure.adapter.output.kafka.events.ProductCreated;
import com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka.mapper.ProductKafkaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@Service("productCreatedProducer")
public class KafkaProductEventPublisherAdapter implements ProductEventPublisherOutputPort {

    private final StreamBridgeWrapper<ProductCreated> productCreatedStreamBridgeWrapper;
    private final ProductKafkaMapper mapper;

    @Override
    public Mono<Void> publish(Product product) {
        return Mono.just(product)
                .map(mapper::toEvent)
                .map(event -> MessageBuilder
                        .withPayload(event)
                        .setHeader(KafkaHeaders.KEY, product.getId())
                        .build())
                .flatMap(message ->
                        Mono.fromCallable(() -> productCreatedStreamBridgeWrapper.send(message))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(sent -> sent ? Mono.empty() : Mono.error(new MessagingException("Kafka send failed")))
                )
                .doOnSuccess(v -> log.info("Product event sent: {}", product.getId()))
                .doOnError(e -> log.error("Mapping/Sending failed", e))
                .then();
    }
}
