package com.abnamro.packageshippingservice.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToUUIDConverter implements Converter<String, UUID> {

    /**
     * Converts a String to a UUID.
     *
     * @param source the String representation of a UUID
     * @return the converted UUID
     * @throws IllegalArgumentException if the source is not a valid UUID format
     */
    @Override
    public UUID convert(String source) {
        try {
            return UUID.fromString(source);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + source);
        }
    }
}

