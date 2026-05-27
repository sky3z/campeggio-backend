package com.campeggio.weather.dto;

import lombok.Data;

@Data
public class WeatherDTO {
    private String city;
    private String description;
    private String icon;
    private double tempCelsius;
    private double feelsLike;
    private int humidity;
    private double windSpeed;
}
