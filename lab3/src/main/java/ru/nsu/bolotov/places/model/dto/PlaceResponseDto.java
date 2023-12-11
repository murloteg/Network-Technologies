package ru.nsu.bolotov.places.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.nsu.bolotov.places.model.dto.opentripmap.InterestingPlacesDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceDescriptionResponseDto;
import ru.nsu.bolotov.places.model.dto.weather.WeatherResponseDto;

public record PlaceResponseDto(
        @JsonProperty(value = "weather")
        WeatherResponseDto weatherResponseDto,
        @JsonProperty(value = "interesting_places")
        InterestingPlacesDto interestingPlacesDto,
        @JsonProperty(value = "place_description")
        PlaceDescriptionResponseDto placeDescriptionResponseDto
) {
}
