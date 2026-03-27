package com.echocano.zestorder.product.infrastructure.adapter.output.persistence.mongodb.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDocument {

    private boolean available;
    private int prepTimeMinutes;
    private int calories;
}
