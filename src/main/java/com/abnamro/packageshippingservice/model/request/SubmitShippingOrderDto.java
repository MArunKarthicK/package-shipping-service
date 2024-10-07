package com.abnamro.packageshippingservice.model.request;


import com.abnamro.packageshippingservice.model.validation.DistinctSenderReceiver;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@DistinctSenderReceiver
public record SubmitShippingOrderDto(

    @NotBlank String packageName,
    @NotNull @Positive Double weightInGrams,
    @NotBlank String receiverEmployeeId,
    @NotBlank String senderEmployeeId

){}
