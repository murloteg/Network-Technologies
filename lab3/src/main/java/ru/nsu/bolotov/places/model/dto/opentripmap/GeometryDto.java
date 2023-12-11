package ru.nsu.bolotov.places.model.dto.opentripmap;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeometryDto(
        @JsonProperty(value = "type")
        String type,
        @JsonProperty(value = "coordinates")
        List<Double> coordinates
) {
}
