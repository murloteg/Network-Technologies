package ru.nsu.bolotov.places.model.dto.geocode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeoCodeResponseDto(
        @JsonProperty(value = "hits")
        List<LocationHitDto> hits,
        @JsonProperty(value = "locale")
        @JsonIgnore
        String locale
) {
}
