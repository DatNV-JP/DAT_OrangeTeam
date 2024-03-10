package com.orange.domain.model;

public enum EOrderStatus {
    COMPLETED(1L),
    CANCEL(2L),
    WAIT_FOR_PAY(3L),
    DELIVERY_IN_PROGRESS(4L),
    WAIT_FOR_CONFIRMATION(5L),
    CONFIRMED(6L),
    WAIT_FOR_CANCEL(7L),
    RETURN(8L),
    WAIT_FOR_RETURN(9L),
    PACKAGING(10L),
    TRANSPORTING(11L);

    private final Long id;

    EOrderStatus(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
