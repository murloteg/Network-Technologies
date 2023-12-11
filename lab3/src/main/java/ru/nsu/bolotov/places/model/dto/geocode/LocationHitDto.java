package ru.nsu.bolotov.places.model.dto.geocode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(value = {"osm_id", "osm_type", "osm_key", "osm_value", "countrycode", "postcode", "housenumber"})
public record LocationHitDto(
        @JsonProperty(value = "point")
        GeocodeLocationPointDto geocodeLocationPointDto,
        @JsonProperty(value = "extent")
        @JsonIgnore
        List<Double> extent,
        @JsonProperty(value = "name")
        String name,
        @JsonProperty(value = "country")
        String country,
        @JsonProperty(value = "state")
        String state,
        @JsonProperty(value = "city")
        String city,
        @JsonProperty(value = "street")
        String street
) {
}
