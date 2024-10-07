package com.abnamro.packageshippingservice.service;

import com.abnamro.packageshippingservice.model.entity.Employee;
import com.abnamro.packageshippingservice.model.response.ReceiverDto;
import com.abnamro.packageshippingservice.repository.EmployeeRepository;
import com.abnamro.packageshippingservice.util.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<ReceiverDto> getAllEmployees() {
        log.info("Received request to list available receivers");
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToReceiverDto).collect(Collectors.toList());
    }

    private ReceiverDto mapToReceiverDto(Employee employee) {

        return ReceiverDto.builder()
                .id(employee.getId())
                .name(ApplicationUtils.concatNames(employee.getFirstName(), employee.getLastName()))
                .streetName(employee.getStreet())
                .postalCode(employee.getPostalCoda())
                .city(employee.getCity())
                .build();
    }
}
