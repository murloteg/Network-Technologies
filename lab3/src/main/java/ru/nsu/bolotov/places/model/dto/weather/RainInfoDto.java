package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RainInfoDto(
        @JsonProperty(value = "1h")
        double volumeFromLastHour
) {
}
