package com.echocano.zestorder.product.application.port.output;

public interface ImageStorageOutputPort {

    String generateUploadUrl(String fileName, String contentType, long contentLength);
}
