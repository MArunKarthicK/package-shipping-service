package com.abnamro.packageshippingservice.service;

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
import com.abnamro.packageshippingservice.repository.ShippingOrderSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShippingOrderServiceImpl implements ShippingOrderService {

    private final ShippingOrderRepository shippingOrderRepository;
    private final EmployeeRepository employeeRepository;
    private final ShippingOrderServiceFactory shippingOrderServiceFactory;

    public ShippingOrderServiceImpl(ShippingOrderRepository shippingOrderRepository, EmployeeRepository employeeRepository, ShippingOrderServiceFactory shippingOrderServiceFactory) {
        this.shippingOrderRepository = shippingOrderRepository;
        this.employeeRepository = employeeRepository;
        this.shippingOrderServiceFactory = shippingOrderServiceFactory;
    }

    @Override
    @Transactional
    public UUID submitShippingOrder(final SubmitShippingOrderDto submitShippingOrderDto) {
        log.info("Submitting shipping order for sender : {}", submitShippingOrderDto.senderEmployeeId());
        Map<Long, Employee> employeeMap = validateAndFetchEmployees(submitShippingOrderDto);

        if (shippingOrderRepository.existsShippingOrderByPackageName(submitShippingOrderDto.packageName())) {
            throw new PackageNameAlreadyExistsException("Package packageName '" + submitShippingOrderDto.packageName() + "' is already taken.");
        }

        ShippingOrder shippingOrderEntity = shippingOrderServiceFactory.buildShippingOrderEntity(submitShippingOrderDto, employeeMap);

        return shippingOrderRepository.save(shippingOrderEntity).getPackageId();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderListDto getShippingOrders(final Long employeeId, final OrderStatusEnum status, final Pageable pageable) {
        log.info("Fetching shipping orders for employee ID: {}", employeeId);
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException("EmployeeId not found");
        }

        Specification<ShippingOrder> specification = ShippingOrderSpecifications.joinEmployeeWithIdAndFilterByPackageStatus(employeeId, status);

        Page<ShippingOrderDto> shippingOrderDtoPages = shippingOrderRepository.findAll(specification, pageable)
                .map(shippingOrderServiceFactory::mapToShippingOrderDto);

        log.info("Successfully retrieved {} shipping orders for employee ID: {}", shippingOrderDtoPages.getTotalElements(), employeeId);
        return shippingOrderServiceFactory.buildShippingOrderDto(shippingOrderDtoPages);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingOrderDetailsDto getShippingOrder(final UUID orderId) {
        log.info("Fetching shipping order for package ID: {}", orderId);

        return shippingOrderRepository.findShippingOrderByPackageId(orderId)
                .map(shippingOrderServiceFactory::mapToShippingOrderDetailsDto)
                .orElseThrow(() -> new ShippingOrderNotFoundException("Shipping order not found"));
    }

    private Map<Long, Employee> validateAndFetchEmployees(SubmitShippingOrderDto submitShippingOrderDto) {
        Long SenderEmpId = parseEmployeeId(submitShippingOrderDto.senderEmployeeId());
        Long receiverEmpId = parseEmployeeId(submitShippingOrderDto.receiverEmployeeId());

        Map<Long, Employee> employeeMap = employeeRepository.findAllById(List.of(SenderEmpId, receiverEmpId))
                .stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        // Instead of validating each employeeId individually and constructing specific error messages,
        // we can also throw an error based on the size of the employee map.
        validateEmployee(employeeMap, submitShippingOrderDto.senderEmployeeId(), "Sender");
        validateEmployee(employeeMap, submitShippingOrderDto.receiverEmployeeId(), "Receiver");

        return employeeMap;
    }

    private void validateEmployee(Map<Long, Employee> employeeMap, String employeeId, String role) {

        Optional.ofNullable(employeeMap.get(Long.valueOf(employeeId)))
                .orElseThrow(() -> new EmployeeNotFoundException(role + " employee not found with ID: " + employeeId));
    }

    private Long parseEmployeeId(String employeeIdStr) {
        try {
            return Long.valueOf(employeeIdStr);
        } catch (NumberFormatException e) {
            throw new InvalidEmployeeIdException("Employee ID is not a valid:" + employeeIdStr);
        }
    }
}