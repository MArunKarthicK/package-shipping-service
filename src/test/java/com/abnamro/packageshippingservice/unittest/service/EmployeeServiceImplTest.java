package com.abnamro.packageshippingservice.unittest.service;

import com.abnamro.packageshippingservice.model.response.ReceiverDto;
import com.abnamro.packageshippingservice.repository.EmployeeRepository;
import com.abnamro.packageshippingservice.service.EmployeeServiceImpl;
import com.abnamro.packageshippingservice.unittest.mock.ApplicationMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @InjectMocks
    private EmployeeServiceImpl classUnderTest;
    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void getAllEmployees_ShouldReturnListOfReceiverDto() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(ApplicationMock.getEmployeeReceiver(), ApplicationMock.getEmployeeSender()));

        List<ReceiverDto> receiverDtoList = classUnderTest.getAllEmployees();

        assertNotNull(receiverDtoList);
        assertEquals(2, receiverDtoList.size());

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getAllEmployees_EmptyList_ShouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList());

        List<ReceiverDto> receiverDtoList = classUnderTest.getAllEmployees();

        assertNotNull(receiverDtoList);
        assertTrue(receiverDtoList.isEmpty());

        verify(employeeRepository, times(1)).findAll();
    }
}
