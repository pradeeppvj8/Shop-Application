package com.pradeep.order_service.exception;

public class ProductNotInStockException extends RuntimeException {
    public ProductNotInStockException(String message) {
        super(message);
    }
}
