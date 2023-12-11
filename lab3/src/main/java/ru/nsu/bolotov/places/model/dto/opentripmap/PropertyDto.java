package ru.nsu.bolotov.places.model.dto.opentripmap;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"dist", "osm", "wikidata"})
public record PropertyDto(
        @JsonProperty(value = "xid")
        String placeId,
        @JsonProperty(value = "name")
        String placeName,
        @JsonProperty(value = "kinds")
        String categories,
        @JsonProperty(value = "rate")
        String rate
) {
}
