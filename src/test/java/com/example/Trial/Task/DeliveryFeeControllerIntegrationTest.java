package com.example.Trial.Task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = TrialTaskApplication.class)
@AutoConfigureMockMvc
public class DeliveryFeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDeliveryFee_Success() throws Exception {
        mockMvc.perform(get("/delivery-fee")
                        .param("city", "Tallinn")
                        .param("vehicleType", "Bike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").isNumber());
    }

    @Test
    public void testGetDeliveryFee_InvalidCity() throws Exception {
        mockMvc.perform(get("/delivery-fee")
                        .param("city", "InvalidCity")
                        .param("vehicleType", "Bike"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid city or vehicle type"));
    }

    @Test
    @Sql(scripts = "/sql/insert-forbidden-weather-data.sql")
    public void testGetDeliveryFee_ForbiddenUsage() throws Exception {
        mockMvc.perform(get("/delivery-fee")
                        .param("city", "Tallinn")
                        .param("vehicleType", "Bike"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usage of selected vehicle type is forbidden"));
    }
}
