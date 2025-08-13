package com.dilaraalk.common.exception;


public class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(Long id) {
        super("Id'si " + id + " olan ürün bulunamadı!");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
