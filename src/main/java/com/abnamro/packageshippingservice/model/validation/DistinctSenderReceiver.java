package com.abnamro.packageshippingservice.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DistinctSenderReceiverValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistinctSenderReceiver {

    String message() default "Sender and Receiver Employee IDs must be different";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
