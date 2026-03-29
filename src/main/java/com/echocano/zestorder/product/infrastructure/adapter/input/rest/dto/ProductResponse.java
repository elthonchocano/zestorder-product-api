package com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto;

import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.util.List;

@Relation(collectionRelation = "products", itemRelation = "product")
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
