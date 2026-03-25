package com.echocano.zestorder.product.infrastructure.adapter.output.mongodb.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationGroupDocument {

    private String groupName;
    private boolean required;
    private List<CustomizationOptionDocument> options;
}
