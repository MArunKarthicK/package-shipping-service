package com.abnamro.packageshippingservice.exception;

public class PackageNameAlreadyExistsException extends RuntimeException {

    public PackageNameAlreadyExistsException(String message) {
        super(message);
    }
}
