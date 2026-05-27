package com.campeggio.weather.controller;

import com.campeggio.weather.dto.WeatherDTO;
import com.campeggio.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService service;

    @GetMapping
    public ResponseEntity<WeatherDTO> getWeather() {
        return ResponseEntity.ok(service.getCurrentWeather());
    }

    @GetMapping("/{city}")
    public ResponseEntity<WeatherDTO> getWeatherByCity(@PathVariable String city) {
        return ResponseEntity.ok(service.getCurrentWeather(city));
    }
}
