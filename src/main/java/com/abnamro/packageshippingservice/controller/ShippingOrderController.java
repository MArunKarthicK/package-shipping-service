package com.abnamro.packageshippingservice.controller;

import com.abnamro.packageshippingservice.exception.ApiErrorResponse;
import com.abnamro.packageshippingservice.model.enums.OrderStatusEnum;
import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.abnamro.packageshippingservice.model.response.OrderListDto;
import com.abnamro.packageshippingservice.model.response.ShippingOrderDetailsDto;
import com.abnamro.packageshippingservice.service.ShippingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/shippingOrders")
@Tag(name = "Shipping Orders", description = "Operations related to shipping orders")
public class ShippingOrderController {

    private final ShippingOrderService shippingOrderService;

    public ShippingOrderController(ShippingOrderService shippingOrderService) {
        this.shippingOrderService = shippingOrderService;
    }

    @PostMapping(produces = "application/json")
    @Operation(
            summary = "Submit a shipping order",
            description = "Submits a new shipping order and returns the location of the created order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Shipping order submitted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict: Package Name already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    public ResponseEntity<?> submitShippingOrder(@RequestBody @Valid SubmitShippingOrderDto submitShippingOrderDto) {
        log.info("Received request to submit a shipping order");
        UUID orderId = shippingOrderService.submitShippingOrder(submitShippingOrderDto);
        log.info("Shipping order submitted successfully with ID: {}", orderId);

        return new ResponseEntity<>(getLocationHeader(orderId), HttpStatus.CREATED);
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get shipping orders", description = "Retrieves a list of shipping orders based on employee ID and package status")
    public ResponseEntity<OrderListDto> getShippingOrders(
            @Parameter(description = "Employee ID", required = true) @RequestParam @NotBlank Long employeeId,
            @Parameter(description = "Package status filter") @RequestParam(required = false) OrderStatusEnum packageStatus,
            @Parameter(description = "Offset for pagination") @RequestParam(required = false, defaultValue = "0") @Min(0) int offset,
            @Parameter(description = "Limit for pagination") @RequestParam(required = false, defaultValue = "10") @Min(1) int limit) {

        log.info("Received request to get shipping orders for employee ID: {}", employeeId);
        OrderListDto orderListDto = shippingOrderService.getShippingOrders(employeeId, packageStatus, PageRequest.of(offset / limit, limit));
        log.info("Successfully retrieved {} shipping orders for employee ID: {}", orderListDto.getOrders().size(), employeeId);

        return new ResponseEntity<>(orderListDto, HttpStatus.OK);
    }

    @GetMapping(path = "/{orderId}", produces = "application/json")
    @Operation(summary = "Get shipping order details", description = "Retrieves details of a specific shipping order by its OrderID")
    public ResponseEntity<ShippingOrderDetailsDto> getShippingOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable @NotBlank UUID orderId) {
        log.info("Received request to get shipping order details for order ID: {}", orderId);
        ShippingOrderDetailsDto shippingOrderDetailsDto = shippingOrderService.getShippingOrder(orderId);
        log.info("Successfully retrieved shipping order details for order ID: {}", orderId);

        return new ResponseEntity<>(shippingOrderDetailsDto, HttpStatus.OK);
    }

    private HttpHeaders getLocationHeader(UUID orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("LOCATION", "/api/shippingOrders/" + orderId);

        return headers;
    }
}

