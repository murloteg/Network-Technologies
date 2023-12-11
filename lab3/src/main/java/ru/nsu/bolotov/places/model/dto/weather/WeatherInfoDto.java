package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherInfoDto(
        @JsonProperty(value = "id")
        int id,
        @JsonProperty(value = "main")
        String weatherType,
        @JsonProperty(value = "description")
        String weatherDescription,
        @JsonIgnore
        @JsonProperty(value = "icon")
        String weatherIcon
) {
}
