package ru.nsu.bolotov.places.model.dto.opentripmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"postcode", "country_code", "path", "pedestrian", "county"})
public record AddressInfoDto(
        @JsonProperty(value = "country")
        String country,
        @JsonProperty(value = "state")
        String state,
        @JsonProperty(value = "suburb")
        String suburb,
        @JsonProperty(value = "city")
        String city,
        @JsonProperty(value = "road")
        String road
) {
}
