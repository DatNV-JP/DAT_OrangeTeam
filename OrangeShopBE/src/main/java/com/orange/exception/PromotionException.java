package com.orange.exception;

public class PromotionException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public PromotionException(String msg) {
        super(msg);
    }

}
