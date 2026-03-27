package com.echocano.zestorder.product.infrastructure.adapter.output.messaging.kafka.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class StreamBridgeWrapper<T> {

    private String binding;
    private StreamBridge streamBridge;

    public boolean send(Message<T> message) {
        return streamBridge.send(binding, message);
    }

}