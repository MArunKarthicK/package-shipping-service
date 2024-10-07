package com.abnamro.packageshippingservice.model.response;

import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.enums.PackageSizeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * ShippingOrderDetails
 */
@Setter
@Getter
@Builder
public class ShippingOrderDetailsDto {

  private String packageId;
  private String packageName;
  private PackageSizeEnum packageSize;
  private String postalCode;
  private String streetName;
  private String receiverName;
  private OrderStatusEnum orderStatus;
  private LocalDate expectedDeliveryDate;
  private OffsetDateTime actualDeliveryDateTime;

}