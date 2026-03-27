package com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationOptionDocument {

    private String name;
    private BigDecimal additionalPrice;
}
