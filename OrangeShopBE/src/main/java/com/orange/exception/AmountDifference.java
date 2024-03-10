package com.orange.exception;

public class AmountDifference extends RuntimeException {
    public AmountDifference(String message) {
        super(message);
    }
}
