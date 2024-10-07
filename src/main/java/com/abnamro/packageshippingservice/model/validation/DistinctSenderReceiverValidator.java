package com.abnamro.packageshippingservice.model.validation;

import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class DistinctSenderReceiverValidator implements ConstraintValidator<DistinctSenderReceiver, SubmitShippingOrderDto> {

    @Override
    public boolean isValid(SubmitShippingOrderDto submitPackageDto, ConstraintValidatorContext constraintValidatorContext) {

        boolean isValid = Optional.ofNullable(submitPackageDto)
                .map(req -> !submitPackageDto.receiverEmployeeId().equalsIgnoreCase(submitPackageDto.senderEmployeeId()))
                .orElse(false);

        if (!isValid) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("senderEmployeeId")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
