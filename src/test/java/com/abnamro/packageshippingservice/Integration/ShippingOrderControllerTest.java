package com.abnamro.packageshippingservice.Integration;

import com.abnamro.packageshippingservice.model.request.SubmitShippingOrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ShippingOrderControllerTest extends BaseIntegrationTest{

    private static final String BASE_URL = "/api/shippingOrders";

    @Autowired
    private MockMvc mockMvc;

    private static UUID orderId;

    @Test
    @Order(1)
    @DisplayName("Submit Shipping Order - Should Return Created")
    void submitShippingOrder_ShouldReturnCreated() throws Exception {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("package1", 1.0, "10001", "10002");

        var result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(submitShippingOrderDto)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(header().exists("LOCATION"))
                .andReturn();

        String locationHeader = result.getResponse().getHeader("LOCATION");
        assertNotNull(locationHeader);
        orderId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf("/") + 1)); // Extract order ID from header
    }

    @Test
    @Order(2)
    @DisplayName("Submit Shipping Order - Should Return Conflict Error")
    void submitShippingOrder_ShouldReturnConflictError() throws Exception {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("package1", 1.0, "10001", "10002");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(submitShippingOrderDto)))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value("Package packageName 'package1' is already taken."));
    }

    @Test
    @Order(3)
    @DisplayName("Submit Shipping Order - Sender and Receiver Should Be Different Error")
    void submitShippingOrder_ShouldReturnSenderandReceiverShouldBeDifferentError() throws Exception {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("package1", 1.0, "10001", "10001");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(submitShippingOrderDto)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid Input Provided"))
                .andExpect(jsonPath("$.errors.senderEmployeeId").value("Sender and Receiver Employee IDs must be different"));
    }

    @Test
    @Order(4)
    @DisplayName("Submit Shipping Order - Weight Should Be Positive Error")
    void submitShippingOrder_ShouldReturnWeightShouldBePostiveError() throws Exception {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("package1", (double) 0, "10001", "10003");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(submitShippingOrderDto)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid Input Provided"))
                .andExpect(jsonPath("$.errors.weightInGrams").value("must be greater than 0"));
    }

    @Test
    @Order(5)
    @DisplayName("Submit Shipping Order - Employee Not Found Error")
    void submitShippingOrder_ShouldReturnEmployeeNotFoundError() throws Exception {
        SubmitShippingOrderDto submitShippingOrderDto = new SubmitShippingOrderDto("package1", 0.1, "10201", "10003");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(submitShippingOrderDto)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Receiver employee not found with ID: 10201"));
    }

    @Test
    @Order(6)
    @DisplayName("Get Shipping Order - Should Return Order Details")
    void getShippingOrder_ShouldReturnOrderDetails() throws Exception {

        if (Objects.isNull(orderId)) {
            submitShippingOrder_ShouldReturnCreated();
            assertNotNull(orderId, "Order ID must be set from the first test");
        }

        mockMvc.perform(get(BASE_URL + "/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.packageId").value(orderId.toString()));
    }


    @Test
    @Order(7)
    @DisplayName("Get Shipping Order - Invalid Input")
    void getShippingOrder_ShouldReturnInvalidInput() throws Exception {
        String randomOrder = "test";

        mockMvc.perform(get(BASE_URL + "/{orderId}", randomOrder))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid UUID string: test"));
    }

    @Test
    @Order(8)
    @DisplayName("Get Shipping Order - Not Found When Order Does Not Exist")
    void getShippingOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();

        mockMvc.perform(get(BASE_URL + "/{orderId}", nonExistentOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Shipping order not found"));
    }

    @Test
    @Order(9)
    @DisplayName("Get Shipping Orders - Should Return List Of Orders")
    void getShippingOrders_ShouldReturnListOfOrders() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("employeeId", "10002")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orders").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @Order(10)
    @DisplayName("Get Shipping Orders - Should Return Empty List")
    void getShippingOrders_ShouldReturnEmptyList() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("employeeId", "10001")
                        .param("packageStatus", "IN_PROGRESS")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orders").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("Get Shipping Orders - Should Return Bad Request")
    void getShippingOrders_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("employeeId", "10001")
                        .param("packageStatus", "PACKED")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    @DisplayName("Get Shipping Orders - Should Return Bad Request When EmployeeId Is Missing")
    void getShippingOrders_ShouldReturnBadRequest_WhenEmployeeIdIsMissing() throws Exception {
        mockMvc.perform(get("/api/shippingOrders")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required parameter 'employeeId' is missing"));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
