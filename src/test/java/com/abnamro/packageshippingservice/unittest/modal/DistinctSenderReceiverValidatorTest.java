package com.abnamro.packageshippingservice.unittest.modal;

import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.validation.DistinctSenderReceiverValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistinctSenderReceiverValidatorTest {

    @InjectMocks
    private DistinctSenderReceiverValidator validator;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintViolationBuilder violationBuilder;
    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;

    @Test
    void isValid_DifferentSenderAndReceiver_ShouldReturnTrue() {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("test", 1.0, "1243", "456");

        boolean result = validator.isValid(submitShippingOrderDto, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void isValid_SameSenderAndReceiver_ShouldReturnFalse() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Sender and Receiver cannot be the same.");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("test", 1.0, "1243", "1243");

        boolean result = validator.isValid(submitShippingOrderDto, context);

        assertFalse(result);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(context).buildConstraintViolationWithTemplate(messageCaptor.capture());
        assertEquals("Sender and Receiver cannot be the same.", messageCaptor.getValue());
        verify(violationBuilder).addPropertyNode("senderEmployeeId");
        verify(nodeBuilder).addConstraintViolation();
    }
}

