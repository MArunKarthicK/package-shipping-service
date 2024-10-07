package com.abnamro.packageshippingservice.exception;

public class ShippingOrderNotFoundException extends RuntimeException {

    public ShippingOrderNotFoundException(String message) {
        super(message);
    }
}
