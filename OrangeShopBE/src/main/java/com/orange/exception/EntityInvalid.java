package com.orange.exception;

public class EntityInvalid extends RuntimeException {
    public EntityInvalid(String message) {
        super(message);
    }
}
