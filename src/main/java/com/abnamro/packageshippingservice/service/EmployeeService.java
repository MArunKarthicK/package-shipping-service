package com.abnamro.packageshippingservice.service;


import com.abnamro.packageshippingservice.model.response.ReceiverDto;

import java.util.List;

public interface EmployeeService {

    List<ReceiverDto> getAllEmployees();

}
