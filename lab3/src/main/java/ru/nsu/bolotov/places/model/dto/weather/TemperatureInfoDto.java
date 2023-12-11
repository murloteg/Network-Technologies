package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TemperatureInfoDto(
        @JsonProperty(value = "temp")
        double temperature,
        @JsonProperty(value = "feels_like")
        double temperatureFeelsLike,
        @JsonProperty(value = "temp_max")
        double temperatureMax,
        @JsonProperty(value = "temp_min")
        double temperatureMin,
        @JsonProperty(value = "pressure")
        int atmospherePressure,
        @JsonProperty(value = "humidity")
        int humidity,
        @JsonIgnore
        @JsonProperty(value = "sea_level")
        int seaLevelPressure,
        @JsonIgnore
        @JsonProperty(value = "grnd_level")
        int groundLevelPressure
) {
}
