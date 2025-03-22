package com.example.Trial.Task.service;
import com.example.Trial.Task.entity.WeatherData;
import com.example.Trial.Task.repository.WeatherDataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Service
public class WeatherImportService {
    private static final String WEATHER_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private final WeatherDataRepository repository;

    @Autowired
    public WeatherImportService(WeatherDataRepository repository) {
        this.repository = repository;
    }
    @PostConstruct
    public void onStartupImport() {
        try {
            importWeatherData();
            System.out.println("Weather data imported on startup.");
        } catch (Exception e) {
            System.err.println("Error during initial weather data import: " + e.getMessage());

        }
    }

    @Scheduled(cron = "${scheduler.cron}")
    public void importWeatherData() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String xml = restTemplate.getForObject(WEATHER_URL, String.class);
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(new InputSource(new StringReader(xml)));
        var observations = doc.getDocumentElement();
        long unixTimestamp = Long.parseLong(observations.getAttribute("timestamp"));
        Instant timestamp = Instant.ofEpochSecond(unixTimestamp);
        ZonedDateTime estonianTime = ZonedDateTime.ofInstant(timestamp, ZoneId.of("Europe/Tallinn"));

        var stations = observations.getElementsByTagName("station");
        var relevantStations = Arrays.asList("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

        for (int i = 0; i < stations.getLength(); i++) {
            var station = (Element) stations.item(i);
            String name = station.getElementsByTagName("name").item(0).getTextContent();
            if (relevantStations.contains(name)) {
                WeatherData data = new WeatherData();
                data.setStationName(name);
                data.setWmoCode(station.getElementsByTagName("wmocode").item(0).getTextContent());
                data.setAirTemperature(Double.parseDouble(station.getElementsByTagName("airtemperature").item(0).getTextContent()));
                data.setWindSpeed(Double.parseDouble(station.getElementsByTagName("windspeed").item(0).getTextContent()));
                data.setWeatherPhenomenon(station.getElementsByTagName("phenomenon").item(0).getTextContent());
                data.setTimestamp(estonianTime.toLocalDateTime());
                repository.save(data);
            }
        }
    }
}