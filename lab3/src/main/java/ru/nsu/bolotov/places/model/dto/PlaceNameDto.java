package ru.nsu.bolotov.places.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record PlaceNameDto(@Valid @NotBlank String placeName) {
}
