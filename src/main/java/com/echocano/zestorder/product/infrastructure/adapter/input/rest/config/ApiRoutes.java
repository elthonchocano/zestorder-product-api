package com.echocano.zestorder.product.infrastructure.adapter.input.rest.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiRoutes {

    public static final String V1_PREFIX = "/api/v1";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Products {
        public static final String BASE = V1_PREFIX + "/products";
        public static final String BY_ID = "/{id}";
        public static final String SEARCH = "/search";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Images {
        public static final String BASE = V1_PREFIX + "/products/images";
        public static final String UPLOAD_URL = "/upload-url";
    }
}
