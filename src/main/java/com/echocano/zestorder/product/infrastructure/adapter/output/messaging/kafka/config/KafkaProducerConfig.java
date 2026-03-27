package com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka.config;

import com.echocano.zestorder.product.infrastructure.adapter.output.kafka.events.ProductCreated;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfig {

    private static final String BINDING_NAME = "productCreatedProducer-out-0";

    @Bean
    public StreamBridgeWrapper<ProductCreated> productCreatedStreamBridgeWrapper(StreamBridge streamBridge) {
        return new StreamBridgeWrapper<>(BINDING_NAME, streamBridge);
    }
}
