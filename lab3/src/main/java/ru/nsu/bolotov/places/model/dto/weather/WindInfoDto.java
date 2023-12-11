package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WindInfoDto(
        @JsonProperty(value = "speed")
        double speed,
        @JsonProperty(value = "deg")
        int windDirectionInDegrees,
        @JsonProperty(value = "gust")
        double windGust
) {
}
