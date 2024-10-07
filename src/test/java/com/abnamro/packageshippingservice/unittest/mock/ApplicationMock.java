package com.abnamro.packageshippingservice.unittest.mock;

import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.enums.PackageSizeEnum;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDto;

import java.time.LocalDate;
import java.util.UUID;

public class ApplicationMock {

    private ApplicationMock(){}

    public static SubmitShippingOrderDto getValidSubmitOrderRequest() {
        return new SubmitShippingOrderDto("package1", 1.0, "10001", "10002");
    }

    public static SubmitShippingOrderDto getInValidSubmitOrderRequest() {
        return new SubmitShippingOrderDto("package2", 2.0, "invalid", "10002");
    }

    public static Employee getEmployeeSender() {
        Employee employee = new Employee();
        employee.setCity("Rotterdam");
        employee.setId(10001L);
        employee.setFirstName("John");
        employee.setLastName("sender");
        employee.setStreet("street 11");
        employee.setPostalCoda("1234Q");

        return employee;
    }

    public static Employee getEmployeeReceiver() {
        Employee employee = new Employee();
        employee.setCity("Rotterdam");
        employee.setId(10002L);
        employee.setFirstName("John");
        employee.setLastName("Receiver");
        employee.setStreet("street 12");
        employee.setPostalCoda("1232Q");

        return employee;
    }

    public static ShippingOrder fillShippingOrder() {
        ShippingOrder shippingOrderEntity = new ShippingOrder();
        shippingOrderEntity.setPackageId(UUID.randomUUID());
        shippingOrderEntity.setPackageName("package1");
        shippingOrderEntity.setWeightInGrams(1.0);
        shippingOrderEntity.setSender(getEmployeeSender());
        shippingOrderEntity.setReceiver(getEmployeeReceiver());
        shippingOrderEntity.setStatus(OrderStatusEnum.IN_PROGRESS);

        return shippingOrderEntity;
    }


    public static ShippingOrderDto fillShippingOrderDto(ShippingOrder shippingOrder) {

        return ShippingOrderDto.builder()
                .packageSize(PackageSizeEnum.L)
                .packageName(shippingOrder.getPackageName())
                .streetName(shippingOrder.getReceiver().getStreet())
                .postalCode(shippingOrder.getReceiver().getPostalCoda())
                .receiverName(shippingOrder.getReceiver().getFirstName())
                .build();
    }

    public static ShippingOrderDetailsDto ShippingOrderDetailsDto(ShippingOrder shippingOrder) {

        return ShippingOrderDetailsDto.builder()
                .orderStatus(shippingOrder.getStatus())
                .packageId(shippingOrder.getPackageId().toString())
                .packageSize(PackageSizeEnum.L)
                .receiverName(shippingOrder.getReceiver().getFirstName())
                .packageName(shippingOrder.getPackageName())
                .streetName(shippingOrder.getReceiver().getStreet())
                .postalCode(shippingOrder.getReceiver().getPostalCoda())
                .expectedDeliveryDate(LocalDate.now())
                .build();
    }
}
