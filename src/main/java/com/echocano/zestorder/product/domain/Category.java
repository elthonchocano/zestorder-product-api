package com.echocano.zestorder.product.domain;

public record Category(
        String name
) {
    public String slug() {
        if (name == null) return "";
        return name.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim()
                .replaceAll(" ", "-")
                .replaceAll("-+", "-");
    }
}
