package ru.nsu.bolotov.places.model.dto.opentripmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(value = {"type"})
public record InterestingPlacesDto(
        @JsonProperty(value = "features")
        List<FeatureDto> features
) {
}
