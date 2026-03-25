package com.echocano.zestorder.product.domain;

public record ProductAttribute(
        boolean isAvailable
        , int prepTimeMinutes
        , int calories
) {
}
