package com.example.Trial.Task;
import com.example.Trial.Task.entity.WeatherData;
import com.example.Trial.Task.enums.City;
import com.example.Trial.Task.enums.VehicleType;
import com.example.Trial.Task.repository.WeatherDataRepository;
import com.example.Trial.Task.service.DeliveryFeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryFeeServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFeeCalculation_Success() throws Exception {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.BIKE;
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setStationName("Tallinn-Harku");
        mockWeatherData.setAirTemperature(-5.0);    // Cold temperature triggers ATEF
        mockWeatherData.setWindSpeed(12.0);         // Moderate wind, no WSEF
        mockWeatherData.setWeatherPhenomenon("snow"); // Snow triggers WPEF
        mockWeatherData.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(weatherDataRepository.findTopByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.of(mockWeatherData));

        // Act: Calculate the delivery fee
        double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);

        assertEquals(5.0, fee); // RBF: 3, ATEF: 0.5, WPEF: 1.5, SUM = 3 + 0.5 + 1.5 = 5
    }

    @Test
    public void testFeeCalculation_ForbiddenUsage() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.BIKE;
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setStationName("Tallinn-Harku");
        mockWeatherData.setAirTemperature(5.0);
        mockWeatherData.setWindSpeed(25.0); // Wind speed > 20 m/s, forbidden for Bike
        mockWeatherData.setWeatherPhenomenon("clear");
        mockWeatherData.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(weatherDataRepository.findTopByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.of(mockWeatherData));

        // Act & Assert: Expect an exception for forbidden usage
        Exception exception = assertThrows(Exception.class, () -> {
            deliveryFeeService.calculateDeliveryFee(city, vehicleType);
        });
        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }

    @Test
    public void testFeeCalculation_NoWeatherData() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.BIKE;
        when(weatherDataRepository.findTopByStationNameOrderByTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            deliveryFeeService.calculateDeliveryFee(city, vehicleType);
        });
        assertEquals("No weather data available for TALLINN", exception.getMessage());
    }
}