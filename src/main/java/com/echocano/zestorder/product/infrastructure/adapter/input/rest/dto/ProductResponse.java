package com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String slug,
        String description,
        String sku,
        BigDecimal basePrice,
        String currency,
        CategoryResponse category
        , List<String> tags
        , List<String> imageUrls
) {
}
