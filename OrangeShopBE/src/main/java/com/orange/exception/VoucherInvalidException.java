package com.orange.exception;

public class VoucherInvalidException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public VoucherInvalidException(String message) {
        super(message);
    }
}
