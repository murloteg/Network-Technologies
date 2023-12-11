package ru.nsu.bolotov.places.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommonLocationPointDto(
        @JsonProperty(value = "lat")
        String latitude,
        @JsonProperty(value = "lon")
        String longitude
) {
}
