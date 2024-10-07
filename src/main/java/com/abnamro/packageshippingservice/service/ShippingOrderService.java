package com.abnamro.packageshippingservice.service;

import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.OrderListDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing shipping orders.
 */
public interface ShippingOrderService {

    /**
     * Submits a new shipping order.
     *
     * @param submitShippingOrderDto the DTO containing the details of the shipping order to be submitted
     * @return the unique identifier (UUID) of the created shipping order
     */
    UUID submitShippingOrder(SubmitShippingOrderDto submitShippingOrderDto);

    /**
     * Retrieves a list of shipping orders for a specific employee.
     *
     * @param employeeId     the ID of the employee whose shipping orders are to be retrieved
     * @param packageStatus  the status filter for the shipping orders (can be null to include all statuses)
     * @param pageable       pagination information including offset and limit for the result set
     * @return an OrderListDto containing the list of shipping orders matching the criteria
     */
    OrderListDto getShippingOrders(Long employeeId, OrderStatusEnum packageStatus, Pageable pageable);

    /**
     * Retrieves the details of a specific shipping order.
     *
     * @param orderId the unique identifier (UUID) of the shipping order to retrieve
     * @return a ShippingOrderDetailsDto containing the details of the requested shipping order
     */
    ShippingOrderDetailsDto getShippingOrder(UUID orderId);
}

