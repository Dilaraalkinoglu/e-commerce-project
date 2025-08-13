package com.dilaraalk.common.exception;

public class ProductListEmptyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductListEmptyException() {
        super("Hiç ürün bulunamadı!");
    }
}
