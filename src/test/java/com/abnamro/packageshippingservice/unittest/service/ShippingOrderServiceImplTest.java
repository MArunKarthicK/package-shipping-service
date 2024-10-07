package com.abnamro.packageshippingservice.unittest.service;

import com.abnamro.packageshippingservice.exception.EmployeeNotFoundException;
import com.abnamro.packageshippingservice.exception.InvalidEmployeeIdException;
import com.abnamro.packageshippingservice.exception.PackageNameAlreadyExistsException;
import com.abnamro.packageshippingservice.exception.ShippingOrderNotFoundException;
import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.entity.ShippingOrder;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.factory.ShippingOrderServiceFactory;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.OrderListDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDto;
import com.abnamro.packageshippingservice.repository.EmployeeRepository;
import com.abnamro.packageshippingservice.repository.ShippingOrderRepository;
import com.abnamro.packageshippingservice.service.ShippingOrderServiceImpl;
import com.abnamro.packageshippingservice.unittest.mock.ApplicationMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingOrderServiceImplTest {

    @Mock
    private ShippingOrderRepository shippingOrderRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ShippingOrderServiceFactory shippingOrderServiceFactory;

    @InjectMocks
    private ShippingOrderServiceImpl classUnderTest;

    @Test
    void submitShippingOrderTest_Success() {
        Employee senderEmployee = ApplicationMock.getEmployeeSender();
        Employee receiverEmployee = ApplicationMock.getEmployeeReceiver();
        SubmitShippingOrderDto submitShippingOrderDto = ApplicationMock.getValidSubmitOrderRequest();
        ShippingOrder shippingOrder =ApplicationMock.fillShippingOrder();
        Map<Long, Employee> employeeMap = Map.of(
                senderEmployee.getId(), senderEmployee,
                receiverEmployee.getId(), receiverEmployee
        );

        when(employeeRepository.findAllById(anyList())).thenReturn(new ArrayList<>(employeeMap.values()));
        when(shippingOrderRepository.existsShippingOrderByPackageName(submitShippingOrderDto.packageName())).thenReturn(false);
        when(shippingOrderServiceFactory.buildShippingOrderEntity(submitShippingOrderDto, employeeMap)).thenReturn(shippingOrder);
        when(shippingOrderRepository.save(shippingOrder)).thenReturn(shippingOrder);

        UUID result = classUnderTest.submitShippingOrder(submitShippingOrderDto);

        assertNotNull(result);
        assertEquals(shippingOrder.getPackageId(), result);

        ArgumentCaptor<SubmitShippingOrderDto> submitShippingOrderDtoArgumentCaptor = ArgumentCaptor.forClass(SubmitShippingOrderDto.class);
        ArgumentCaptor<Map<Long, Employee>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(shippingOrderServiceFactory).buildShippingOrderEntity(submitShippingOrderDtoArgumentCaptor.capture(), mapCaptor.capture());
        assertEquals(submitShippingOrderDto, submitShippingOrderDtoArgumentCaptor.getValue());
        assertEquals(employeeMap, mapCaptor.getValue());

        verify(employeeRepository, times(1)).findAllById(anyList());
        verify(shippingOrderRepository, times(1)).existsShippingOrderByPackageName("package1");
        verify(shippingOrderServiceFactory, times(1)).buildShippingOrderEntity(submitShippingOrderDto, employeeMap);
        verify(shippingOrderRepository, times(1)).save(shippingOrder);
    }

    @Test
    void submitShippingOrderTest_DuplicatePackageName_ShouldThrowPackageNameAlreadyExistsException() {
        Employee senderEmployee = ApplicationMock.getEmployeeSender();
        Employee receiverEmployee = ApplicationMock.getEmployeeReceiver();
        SubmitShippingOrderDto submitShippingOrderDto = ApplicationMock.getValidSubmitOrderRequest();
        Map<Long, Employee> employeeMap = Map.of(
                senderEmployee.getId(), senderEmployee,
                receiverEmployee.getId(), receiverEmployee
        );

        when(employeeRepository.findAllById(anyList())).thenReturn(new ArrayList<>(employeeMap.values()));
        when(shippingOrderRepository.existsShippingOrderByPackageName(submitShippingOrderDto.packageName())).thenReturn(true);

        PackageNameAlreadyExistsException exception = assertThrows(
                PackageNameAlreadyExistsException.class,
                () -> classUnderTest.submitShippingOrder(submitShippingOrderDto)
        );

        assertEquals("Package packageName 'package1' is already taken.", exception.getMessage());

        verify(shippingOrderRepository, times(1)).existsShippingOrderByPackageName("package1");
        verify(shippingOrderServiceFactory, never()).buildShippingOrderEntity(any(), any());
        verify(shippingOrderRepository, never()).save(any());
    }

    @Test
    void submitShippingOrderTest_InvalidSenderEmployeeId_InvalidEmployeeIdException() {
        SubmitShippingOrderDto inValidSubmitOrderRequest = ApplicationMock.getInValidSubmitOrderRequest();

        InvalidEmployeeIdException exception = assertThrows(
                InvalidEmployeeIdException.class,
                () -> classUnderTest.submitShippingOrder(inValidSubmitOrderRequest)
        );

        assertEquals("Employee ID is not a valid:invalid", exception.getMessage());

        verify(employeeRepository, never()).findAllById(anyList());
        verify(shippingOrderRepository, never()).existsShippingOrderByPackageName(anyString());
        verify(shippingOrderServiceFactory, never()).buildShippingOrderEntity(any(), anyMap());
        verify(shippingOrderRepository, never()).save(any());
    }

    @Test
    void submitShippingOrderTest_SenderEmployeeNotFound_ShouldThrowEmployeeNotFoundException() {
        when(employeeRepository.findAllById(List.of(10002L, 10001L))).thenReturn(List.of(ApplicationMock.getEmployeeSender()));

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> classUnderTest.submitShippingOrder(ApplicationMock.getValidSubmitOrderRequest())
        );

        assertEquals("Sender employee not found with ID: 10002", exception.getMessage());

        verify(employeeRepository, times(1)).findAllById(List.of(10002L, 10001L));
        verify(shippingOrderRepository, never()).existsShippingOrderByPackageName(anyString());
        verify(shippingOrderServiceFactory, never()).buildShippingOrderEntity(any(), anyMap());
        verify(shippingOrderRepository, never()).save(any());
    }

    @Test
    void submitShippingOrderTest_ReceiverEmployeeNotFound_ShouldThrowEmployeeNotFoundException() {
        when(employeeRepository.findAllById(List.of(10002L, 10001L))).thenReturn(List.of(ApplicationMock.getEmployeeReceiver()));

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> classUnderTest.submitShippingOrder(ApplicationMock.getValidSubmitOrderRequest())
        );

        assertEquals("Receiver employee not found with ID: 10001", exception.getMessage());

        verify(employeeRepository, times(1)).findAllById(List.of(10002L, 10001L));
        verify(shippingOrderRepository, never()).existsShippingOrderByPackageName(anyString());
        verify(shippingOrderServiceFactory, never()).buildShippingOrderEntity(any(), anyMap());
        verify(shippingOrderRepository, never()).save(any());
    }

    @Test
    void getShippingOrdersTest_Success() {
        Long employeeId = 10001L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("packageId").descending());
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();
        ShippingOrderDto shippingOrderDto = ApplicationMock.fillShippingOrderDto(shippingOrder);
        Page<ShippingOrder> shippingOrderPage = new PageImpl<>(List.of(shippingOrder), pageable, 1);
        Page<ShippingOrderDto> dtoPage = new PageImpl<>(List.of(shippingOrderDto), pageable, 1);

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(shippingOrderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(shippingOrderPage);
        when(shippingOrderServiceFactory.mapToShippingOrderDto(shippingOrder)).thenReturn(shippingOrderDto);
        when(shippingOrderServiceFactory.buildShippingOrderDto(dtoPage)).thenReturn(new OrderListDto(List.of(shippingOrderDto), 1));

        OrderListDto result = classUnderTest.getShippingOrders(employeeId, OrderStatusEnum.IN_PROGRESS, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getOrders().size());
        assertEquals("package1", result.getOrders().get(0).getPackageName());

        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(shippingOrderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(shippingOrderServiceFactory, times(1)).mapToShippingOrderDto(shippingOrder);
        verify(shippingOrderServiceFactory, times(1)).buildShippingOrderDto(dtoPage);
    }

    @Test
    void getShippingOrdersTestBasedOnEmployeeId_Success() {
        Long employeeId = 10001L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("packageId").descending());
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();
        ShippingOrderDto shippingOrderDto = ApplicationMock.fillShippingOrderDto(shippingOrder);
        Page<ShippingOrder> shippingOrderPage = new PageImpl<>(List.of(shippingOrder), pageable, 1);
        Page<ShippingOrderDto> dtoPage = new PageImpl<>(List.of(shippingOrderDto), pageable, 1);

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(shippingOrderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(shippingOrderPage);
        when(shippingOrderServiceFactory.mapToShippingOrderDto(shippingOrder)).thenReturn(shippingOrderDto);
        when(shippingOrderServiceFactory.buildShippingOrderDto(dtoPage)).thenReturn(new OrderListDto(List.of(shippingOrderDto), 1));

        OrderListDto result = classUnderTest.getShippingOrders(employeeId, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getOrders().size());
        assertEquals("package1", result.getOrders().get(0).getPackageName());

        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(shippingOrderRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(shippingOrderServiceFactory, times(1)).mapToShippingOrderDto(shippingOrder);
        verify(shippingOrderServiceFactory, times(1)).buildShippingOrderDto(dtoPage);
    }

    @Test
    void getShippingOrdersTest_EmployeeNotFound_ShouldThrowEmployeeNotFoundException() {
        Long employeeId = 10099L;
        Pageable pageable = PageRequest.of(0, 10);

        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> classUnderTest.getShippingOrders(employeeId, OrderStatusEnum.IN_PROGRESS, pageable)
        );

        assertEquals("EmployeeId not found", exception.getMessage());

        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(shippingOrderRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        verify(shippingOrderServiceFactory, never()).mapToShippingOrderDto(any());
        verify(shippingOrderServiceFactory, never()).buildShippingOrderDto(any());
    }

    @Test
    void getShippingOrderTest_Success() {
        ShippingOrder shippingOrder = ApplicationMock.fillShippingOrder();
        UUID orderId = shippingOrder.getPackageId();
        ShippingOrderDetailsDto shippingOrderDetailsDto = ApplicationMock.ShippingOrderDetailsDto(shippingOrder);

        when(shippingOrderRepository.findShippingOrderByPackageId(orderId)).thenReturn(Optional.of(shippingOrder));
        when(shippingOrderServiceFactory.mapToShippingOrderDetailsDto(shippingOrder)).thenReturn(shippingOrderDetailsDto);

        ShippingOrderDetailsDto result = classUnderTest.getShippingOrder(orderId);

        assertNotNull(result);
        assertEquals(orderId.toString(), result.getPackageId());
        assertEquals("package1", result.getPackageName());

        verify(shippingOrderRepository, times(1)).findShippingOrderByPackageId(orderId);
        verify(shippingOrderServiceFactory, times(1)).mapToShippingOrderDetailsDto(shippingOrder);
    }

    @Test
    void getShippingOrder_NotFound_ShouldThrowException() {
        UUID nonExistentOrderId = UUID.randomUUID();

        when(shippingOrderRepository.findShippingOrderByPackageId(nonExistentOrderId)).thenReturn(Optional.empty());

        ShippingOrderNotFoundException exception = assertThrows(
                ShippingOrderNotFoundException.class,
                () -> classUnderTest.getShippingOrder(nonExistentOrderId)
        );

        assertEquals("Shipping order not found", exception.getMessage());

        verify(shippingOrderRepository, times(1)).findShippingOrderByPackageId(nonExistentOrderId);
        verify(shippingOrderServiceFactory, never()).mapToShippingOrderDetailsDto(any());
    }
}

