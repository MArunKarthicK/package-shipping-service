package com.abnamro.packageshippingservice.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiverDto {

    private long id;
    private String name;
    private String streetName;
    private String postalCode;
    private String city;

}
