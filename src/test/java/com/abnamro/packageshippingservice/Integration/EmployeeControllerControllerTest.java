package com.abnamro.packageshippingservice.Integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerControllerTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET ListAvailableReceivers - Success")
    public void testListAvailableReceivers_Success() throws Exception {
        mockMvc.perform(get("/api/employees/receivers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].id", everyItem(notNullValue())));

    }
}
