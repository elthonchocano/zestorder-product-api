package com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100)
        String name,
        String description,
        @NotBlank(message = "SKU is required")
        String sku,
        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be greater than zero")
        BigDecimal basePrice,
        @NotBlank(message = "Currency code is required")
        @Size(min = 3, max = 3)
        String currency,
        @NotBlank(message = "Category name is required")
        String category
        , List<String> tags
        , List<String> imageUrls
) {
}
