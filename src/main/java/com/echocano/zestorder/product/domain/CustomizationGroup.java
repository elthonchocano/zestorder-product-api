package com.echocano.zestorder.product.domain;

import java.util.List;

public record CustomizationGroup(
        String groupName
        , boolean isRequired
        , List<CustomizationOption> options
) {
}
