package com.example.Trial.Task.controller;

import com.example.Trial.Task.enums.City;
import com.example.Trial.Task.enums.VehicleType;
import com.example.Trial.Task.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    @Autowired
    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping("/delivery-fee")
    @Operation(summary = "Calculate delivery fee", description = "Calculates the delivery fee based on city and vehicle type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = FeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or usage forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getDeliveryFee(
            @Parameter(description = "City name (Tallinn, Tartu, Pärnu)", required = true) @RequestParam String city,
            @Parameter(description = "Vehicle type (Car, Scooter, Bike)", required = true) @RequestParam String vehicleType) {
        try {
            // Converts string inputs to enums
            City cityEnum = City.valueOf(city.toUpperCase());
            VehicleType vehicleEnum = VehicleType.valueOf(vehicleType.toUpperCase());

            // Calculates fee using the service
            double fee = deliveryFeeService.calculateDeliveryFee(cityEnum, vehicleEnum);

            // Returns success response
            return ResponseEntity.ok(new FeeResponse(fee));
        } catch (IllegalArgumentException e) {
            // Handles invalid city or vehicle type
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid city or vehicle type"));
        } catch (Exception e) {
            // Handles service exceptions (e.g., forbidden vehicle usage)
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Response class for successful fee calculation
    @Getter
    public static class FeeResponse {
        private double fee;

        public FeeResponse(double fee) {
            this.fee = fee;
        }

    }

    // Response class for errors
    @Getter
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

    }
}
