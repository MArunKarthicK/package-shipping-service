package com.abnamro.packageshippingservice.model.enums;

public enum OrderStatusEnum {

    IN_PROGRESS("IN_PROGRESS"),

    SENT("SENT"),

    DELIVERED("DELIVERED");

    private String value;

    OrderStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
