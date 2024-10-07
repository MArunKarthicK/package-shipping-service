  package com.abnamro.packageshippingservice.model.response;

  import com.abnamro.packageshippingservice.model.enums.PackageSizeEnum;
  import lombok.Builder;
  import lombok.Getter;
  import lombok.Setter;

  @Builder
  @Setter
  @Getter
  public class ShippingOrderDto {

    private String packageName;
    private String postalCode;
    private String streetName;
    private String receiverName;
    private PackageSizeEnum packageSize;

  }

