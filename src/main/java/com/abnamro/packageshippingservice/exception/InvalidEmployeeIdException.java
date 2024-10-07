package com.abnamro.packageshippingservice.exception;

public class InvalidEmployeeIdException extends RuntimeException {

    public InvalidEmployeeIdException(String message) {
        super(message);
    }
}
