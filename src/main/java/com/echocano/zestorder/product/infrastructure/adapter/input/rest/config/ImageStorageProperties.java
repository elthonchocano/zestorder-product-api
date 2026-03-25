package com.echocano.zestorder.product.infrastructure.adapter.input.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "zestorder.product.images")
public class ImageStorageProperties {
    private int minCount;
    private int maxCount;
    private long maxSizeBytes;
    private List<String> allowedTypes;
}
