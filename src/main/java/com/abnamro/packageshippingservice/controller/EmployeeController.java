package com.abnamro.packageshippingservice.controller;


import com.abnamro.packageshippingservice.model.response.ReceiverDto;
import com.abnamro.packageshippingservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management Service", description = "Operations to manage and fetch employees details")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(path = "/receivers", produces = "application/json")
    @Operation(summary = "List available receivers", description = "Returns a list of available receivers from the employee service")
    public ResponseEntity<List<ReceiverDto>> listAvailableReceivers() {
        log.info("Received request to list available receivers");
        List<ReceiverDto> receivers = employeeService.getAllEmployees();
        log.info("Successfully retrieved {} receivers.", receivers.size());
        return new ResponseEntity<>(receivers, HttpStatus.OK);
    }
}


