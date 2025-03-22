package com.example.Trial.Task.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stationName;
    private String wmoCode;
    private Double airTemperature;
    private Double windSpeed;
    private String weatherPhenomenon;
    private LocalDateTime timestamp;
}
