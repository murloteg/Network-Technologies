package ru.nsu.bolotov.places.model.dto.opentripmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"type", "id"})
public record FeatureDto(
        @JsonProperty(value = "geometry")
        GeometryDto geometryDto,
        @JsonProperty(value = "properties")
        PropertyDto propertyDto
) {
}
