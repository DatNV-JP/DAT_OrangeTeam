package com.orange.exception;

public class InvalidChecksum extends RuntimeException {
    public InvalidChecksum(String message) {
        super(message);
    }
}
