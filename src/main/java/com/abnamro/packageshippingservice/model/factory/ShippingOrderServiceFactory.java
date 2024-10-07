package com.abnamro.packageshippingservice.model.factory;


import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.enums.PackageSizeEnum;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.OrderListDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDto;
import com.abnamro.packageshippingservice.util.ApplicationUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class ShippingOrderServiceFactory {

    public ShippingOrder buildShippingOrderEntity(SubmitShippingOrderDto submitShippingOrderDto, Map<Long, Employee> employeeMap) {

        return ShippingOrder.builder()
                .packageName(submitShippingOrderDto.packageName())
                .weightInGrams(submitShippingOrderDto.weightInGrams())
                .status(OrderStatusEnum.IN_PROGRESS)
                .sender(employeeMap.get(Long.valueOf(submitShippingOrderDto.senderEmployeeId())))
                .receiver(employeeMap.get(Long.valueOf(submitShippingOrderDto.receiverEmployeeId())))
                .packageId(UUID.randomUUID())
                .build();
    }

    public ShippingOrderDetailsDto mapToShippingOrderDetailsDto(ShippingOrder shippingOrder) {
        ShippingOrderDetailsDto.ShippingOrderDetailsDtoBuilder builder = ShippingOrderDetailsDto.builder()
                .packageId(shippingOrder.getPackageId().toString())
                .packageName(shippingOrder.getPackageName())
                .postalCode(shippingOrder.getReceiver().getPostalCoda())
                .streetName(shippingOrder.getReceiver().getStreet())
                .receiverName(ApplicationUtils.concatNames(shippingOrder.getReceiver().getFirstName(), shippingOrder.getReceiver().getLastName()))
                .orderStatus(shippingOrder.getStatus())
                .packageSize(PackageSizeEnum.fromWeight(shippingOrder.getWeightInGrams()));

        if (Objects.nonNull(shippingOrder.getDeliveredDate())) {
            builder.actualDeliveryDateTime(shippingOrder.getDeliveredDate().atOffset(ZoneOffset.UTC));
        } else {
            builder.expectedDeliveryDate(ApplicationUtils.calculateExpectedDeliveryDate(1));
        }

        return builder.build();
    }

    public ShippingOrderDto mapToShippingOrderDto(ShippingOrder shippingOrder) {

        return ShippingOrderDto.builder().packageName(shippingOrder.getPackageName())
                .postalCode(shippingOrder.getReceiver().getPostalCoda())
                .streetName(shippingOrder.getReceiver().getStreet())
                .receiverName(ApplicationUtils.concatNames(shippingOrder.getReceiver().getFirstName(),shippingOrder.getReceiver().getLastName()))
                .packageSize(PackageSizeEnum.fromWeight(shippingOrder.getWeightInGrams()))
                .build();
    }

    public OrderListDto buildShippingOrderDto(Page<ShippingOrderDto> shippingOrderDtoPages) {

        return new OrderListDto(shippingOrderDtoPages.getContent(), (int) shippingOrderDtoPages.getTotalElements());
    }
}
