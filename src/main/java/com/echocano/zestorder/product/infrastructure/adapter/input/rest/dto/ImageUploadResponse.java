package com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto;

public record ImageUploadResponse(
        String uploadUrl
        , String finalFileName
) {
}
