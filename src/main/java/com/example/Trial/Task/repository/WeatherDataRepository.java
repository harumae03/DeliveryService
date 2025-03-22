package com.example.Trial.Task.repository;
import com.example.Trial.Task.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findTopByStationNameOrderByTimestampDesc(String stationName);
}
