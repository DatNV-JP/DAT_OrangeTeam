package com.orange.exception;

public class OrderUpdateException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public OrderUpdateException(String message) {
        super(message);
    }
}
