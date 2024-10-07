package com.abnamro.packageshippingservice.unittest.modal;

import com.abnamro.packageshippingservice.model.converter.StringToUUIDConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StringToUUIDConverterTest {

    @InjectMocks
    private StringToUUIDConverter converter;

    @Test
    void convert_ValidUUID_ShouldReturnUUID() {
        String validUUIDString = "123e4567-e89b-12d3-a456-426614174000";

        UUID result = converter.convert(validUUIDString);

        assertNotNull(result);
        assertEquals(UUID.fromString(validUUIDString), result);
    }

    @Test
    void convert_InvalidUUID_ShouldThrowIllegalArgumentException() {
        String invalidUUIDString = "invalid-uuid-string";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(invalidUUIDString);
        });

        assertEquals("Invalid UUID format: " + invalidUUIDString, exception.getMessage());
    }

    @Test
    void convert_EmptyString_ShouldThrowIllegalArgumentException() {
        String emptyUUIDString = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            converter.convert(emptyUUIDString);
        });

        assertEquals("Invalid UUID format: " + emptyUUIDString, exception.getMessage());
    }
}

