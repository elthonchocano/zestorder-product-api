package com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto;

public record ImageUploadRequest(
        String fileName
        , String contentType
        , long sizeInBytes
) {
}
