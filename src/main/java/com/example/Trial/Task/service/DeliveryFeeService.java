package com.example.Trial.Task.service;

import com.example.Trial.Task.entity.WeatherData;
import com.example.Trial.Task.enums.City;
import com.example.Trial.Task.enums.VehicleType;
import com.example.Trial.Task.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
public class DeliveryFeeService {
    private final WeatherDataRepository repository;
    private static final Map<City, String> CITY_TO_STATION = Map.of(
            City.TALLINN, "Tallinn-Harku",
            City.TARTU, "Tartu-Tõravere",
            City.PÄRNU, "Pärnu"
    );

    @Autowired
    public DeliveryFeeService(WeatherDataRepository repository) {
        this.repository = repository;
    }

    public double calculateDeliveryFee(City city, VehicleType vehicleType) {
        String station = CITY_TO_STATION.get(city);
        Optional<WeatherData> weatherOpt = repository.findTopByStationNameOrderByTimestampDesc(station);
        if (weatherOpt.isEmpty()) {
            throw new RuntimeException("No weather data available for " + city);
        }
        WeatherData weather = weatherOpt.get();

        if (isUsageForbidden(vehicleType, weather)) {
            throw new RuntimeException("Usage of selected vehicle type is forbidden");
        }

        double rbf = getRegionalBaseFee(city, vehicleType);
        double atef = getAirTemperatureExtraFee(vehicleType, weather.getAirTemperature());
        double wsef = getWindSpeedExtraFee(vehicleType, weather.getWindSpeed());
        double wpef = getWeatherPhenomenonExtraFee(vehicleType, weather.getWeatherPhenomenon());
        return rbf + atef + wsef + wpef;
    }

    private boolean isUsageForbidden(VehicleType vehicleType, WeatherData weather) {
        if (vehicleType == VehicleType.BIKE && weather.getWindSpeed() > 20) return true;
        String phenomenon = weather.getWeatherPhenomenon().toLowerCase();
        return (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) &&
                (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder"));
    }

    private double getRegionalBaseFee(City city, VehicleType vehicleType) {
        if (city == City.TALLINN) {
            return switch (vehicleType) {
                case CAR -> 4.0;
                case SCOOTER -> 3.5;
                case BIKE -> 3.0;
            };
        } else if (city == City.TARTU) {
            return switch (vehicleType) {
                case CAR -> 3.5;
                case SCOOTER -> 3.0;
                case BIKE -> 2.5;
            };
        } else {
            return switch (vehicleType) {
                case CAR -> 3.0;
                case SCOOTER -> 2.5;
                case BIKE -> 2.0;
            };
        }
    }

    private double getAirTemperatureExtraFee(VehicleType vehicleType, Double temp) {
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            if (temp < -10) return 1.0;
            if (temp >= -10 && temp < 0) return 0.5;
        }
        return 0.0;
    }

    private double getWindSpeedExtraFee(VehicleType vehicleType, Double windSpeed) {
        if (vehicleType == VehicleType.BIKE && windSpeed >= 10 && windSpeed < 20) return 0.5;
        return 0.0;
    }

    private double getWeatherPhenomenonExtraFee(VehicleType vehicleType, String phenomenon) {
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            String lower = phenomenon.toLowerCase();
            if (lower.contains("snow") || lower.contains("sleet")) return 1.0;
            if (lower.contains("rain")) return 0.5;
        }
        return 0.0;
    }
}
