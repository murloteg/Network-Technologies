package ru.nsu.bolotov.places.model.dto.geocode;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeocodeLocationPointDto(
        @JsonProperty(value = "lat")
        String latitude,
        @JsonProperty(value = "lng")
        String longitude
) {
}
