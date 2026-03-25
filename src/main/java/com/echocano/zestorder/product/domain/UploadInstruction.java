package com.echocano.zestorder.product.domain;

public record UploadInstruction(
        String fileName
        , String contentType
        , long sizeInBytes
) {
}
