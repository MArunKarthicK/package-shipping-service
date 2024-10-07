package com.abnamro.packageshippingservice.unittest.modal;

import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.enums.PackageSizeEnum;
import com.abnamro.packageshippingservice.model.factory.ShippingOrderServiceFactory;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.OrderListDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDto;
import com.abnamro.packageshippingservice.unittest.mock.ApplicationMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ShippingOrderServiceFactoryTest {

    @InjectMocks
    private ShippingOrderServiceFactory classUnderTest;

    @Test
    void buildShippingOrderEntity_ShouldReturnShippingOrder() {
        SubmitShippingOrderDto submitShippingOrderDto = ApplicationMock.getValidSubmitOrderRequest();
        Employee senderEmployee = ApplicationMock.getEmployeeSender();
        Employee receiverEmployee = ApplicationMock.getEmployeeReceiver();
        Map<Long, Employee> employeeMap = new HashMap<>();
        employeeMap.put(Long.valueOf(submitShippingOrderDto.senderEmployeeId()), senderEmployee);
        employeeMap.put(Long.valueOf(submitShippingOrderDto.receiverEmployeeId()), receiverEmployee);

        ShippingOrder shippingOrder = classUnderTest.buildShippingOrderEntity(submitShippingOrderDto, employeeMap);

        assertNotNull(shippingOrder);
        assertEquals(submitShippingOrderDto.packageName(), shippingOrder.getPackageName());
        assertEquals(submitShippingOrderDto.weightInGrams(), shippingOrder.getWeightInGrams());
        assertEquals(OrderStatusEnum.IN_PROGRESS, shippingOrder.getStatus());
        assertEquals(senderEmployee, shippingOrder.getSender());
        assertEquals(receiverEmployee, shippingOrder.getReceiver());
    }

    @Test
    void mapToShippingOrderDetailsDto_ShouldReturnShippingOrderDetailsDto() {
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();

        ShippingOrderDetailsDto detailsDto = classUnderTest.mapToShippingOrderDetailsDto(shippingOrder);

        assertNotNull(detailsDto);
        assertEquals(shippingOrder.getPackageId().toString(), detailsDto.getPackageId());
        assertEquals(shippingOrder.getPackageName(), detailsDto.getPackageName());
        assertEquals(shippingOrder.getReceiver().getPostalCoda(), detailsDto.getPostalCode());
        assertEquals(shippingOrder.getReceiver().getStreet(), detailsDto.getStreetName());
        assertEquals(shippingOrder.getReceiver().getFirstName() + " " + shippingOrder.getReceiver().getLastName(), detailsDto.getReceiverName());
        assertEquals(OrderStatusEnum.IN_PROGRESS, detailsDto.getOrderStatus());
        assertEquals(PackageSizeEnum.fromWeight(shippingOrder.getWeightInGrams()), detailsDto.getPackageSize());
    }

    @Test
    void mapToShippingOrderDto_ShouldReturnShippingOrderDto() {
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();

        ShippingOrderDto orderDto = classUnderTest.mapToShippingOrderDto(shippingOrder);

        assertNotNull(orderDto);
        assertEquals(orderDto.getPackageName(), orderDto.getPackageName());
        assertEquals(shippingOrder.getReceiver().getPostalCoda(), orderDto.getPostalCode());
        assertEquals(shippingOrder.getReceiver().getStreet(), orderDto.getStreetName());
        assertEquals(shippingOrder.getReceiver().getFirstName() + " " + shippingOrder.getReceiver().getLastName(), orderDto.getReceiverName());
        assertEquals(PackageSizeEnum.fromWeight(shippingOrder.getWeightInGrams()), orderDto.getPackageSize());
    }

    @Test
    void buildShippingOrderDto_ShouldReturnOrderListDto() {
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();
        ShippingOrderDto shippingOrderDto = classUnderTest.mapToShippingOrderDto(shippingOrder);

        List<ShippingOrderDto> shippingOrderDtoList = List.of(shippingOrderDto);
        Page<ShippingOrderDto> page = new PageImpl<>(shippingOrderDtoList);

        OrderListDto orderListDto = classUnderTest.buildShippingOrderDto(page);

        assertNotNull(orderListDto);
        assertEquals(1, orderListDto.getTotal());
        assertEquals(shippingOrderDtoList, orderListDto.getOrders());
    }
}

