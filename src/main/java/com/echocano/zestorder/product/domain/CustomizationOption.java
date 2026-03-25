package com.echocano.zestorder.product.domain;

import java.math.BigDecimal;

public record CustomizationOption(
        String name
        , BigDecimal additionalPrice
) {
}
