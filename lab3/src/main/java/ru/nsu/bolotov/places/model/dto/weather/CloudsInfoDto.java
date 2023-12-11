package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloudsInfoDto(
        @JsonProperty(value = "all")
        int cloudiness
) {
}
