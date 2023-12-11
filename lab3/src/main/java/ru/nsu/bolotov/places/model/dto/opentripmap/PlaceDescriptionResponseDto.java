package ru.nsu.bolotov.places.model.dto.opentripmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.nsu.bolotov.places.model.dto.CommonLocationPointDto;

@JsonIgnoreProperties(value = {"osm", "wikidata", "image", "preview", "wikipedia_extracts", "voyage", "sources", "bbox"})
public record PlaceDescriptionResponseDto(
        @JsonProperty(value = "xid")
        String placeId,
        @JsonProperty(value = "name")
        String placeName,
        @JsonProperty(value = "kinds")
        String categories,
        @JsonProperty(value = "rate")
        String rate,
        @JsonProperty(value = "wikipedia")
        String wikipediaLink,
        @JsonProperty(value = "url")
        String placeWebSiteLink,
        @JsonProperty(value = "otm")
        String placeOpenTripMapLink,
        @JsonProperty(value = "address")
        AddressInfoDto addressInfoDto,
        @JsonProperty(value = "point")
        CommonLocationPointDto locationPointDto,
        @JsonProperty(value = "info")
        SimplePlaceDescriptionDto simplePlaceDescriptionDto
) {
}
