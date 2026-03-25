package com.echocano.zestorder.product.application.exception;

public class ProductWasDeletedException extends RuntimeException {

    public ProductWasDeletedException(String message) {
        super(message);
    }
}
