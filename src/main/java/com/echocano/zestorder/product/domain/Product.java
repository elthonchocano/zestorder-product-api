package com.echocano.zestorder.product.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Product {
    String id;
    String name;
    String description;
    String sku;
    BigDecimal basePrice;
    Category category;
    ProductAttribute attribute;
    List<CustomizationGroup> customizations;
    String currencyCode;
    Set<String> tags;
    List<String> imageUrls;
    ProductStatus status;
    Instant createdAt;
    Instant updatedAt;

    public String slug() {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim()
                .replaceAll(" ", "-")
                .replaceAll("-+", "-");
    }
}
