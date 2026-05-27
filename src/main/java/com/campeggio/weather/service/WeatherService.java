package com.campeggio.weather.service;

import com.campeggio.weather.dto.WeatherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final WebClient.Builder webClientBuilder;

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.city:Castello Tesino,IT}")
    private String defaultCity;

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";

    public WeatherDTO getCurrentWeather() {
        return getCurrentWeather(defaultCity);
    }

    @SuppressWarnings("unchecked")
    public WeatherDTO getCurrentWeather(String city) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenWeatherMap API key non configurata — restituisco dati di esempio");
            return mockWeather(city);
        }

        try {
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri(BASE_URL + "/weather?q={city}&appid={key}&units=metric&lang=it",
                            city, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseResponse(response);
        } catch (Exception e) {
            log.error("Errore nel recupero meteo: {}", e.getMessage());
            return mockWeather(city);
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherDTO parseResponse(Map<String, Object> response) {
        WeatherDTO dto = new WeatherDTO();

        dto.setCity((String) response.get("name"));

        var weatherList = (java.util.List<Map<String, Object>>) response.get("weather");
        if (weatherList != null && !weatherList.isEmpty()) {
            dto.setDescription((String) weatherList.get(0).get("description"));
            dto.setIcon((String) weatherList.get(0).get("icon"));
        }

        var main = (Map<String, Object>) response.get("main");
        if (main != null) {
            dto.setTempCelsius(((Number) main.get("temp")).doubleValue());
            dto.setFeelsLike(((Number) main.get("feels_like")).doubleValue());
            dto.setHumidity(((Number) main.get("humidity")).intValue());
        }

        var wind = (Map<String, Object>) response.get("wind");
        if (wind != null) {
            dto.setWindSpeed(((Number) wind.get("speed")).doubleValue());
        }

        return dto;
    }

    private WeatherDTO mockWeather(String city) {
        WeatherDTO dto = new WeatherDTO();
        dto.setCity(city);
        dto.setDescription("sereno");
        dto.setIcon("01d");
        dto.setTempCelsius(28.0);
        dto.setFeelsLike(30.0);
        dto.setHumidity(65);
        dto.setWindSpeed(3.5);
        return dto;
    }
}
