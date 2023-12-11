package ru.nsu.bolotov.places.model.dto.opentripmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = {"src_id", "src", "image", "img_width", "img_height"})
public record SimplePlaceDescriptionDto(
        @JsonProperty(value = "descr")
        String description
) {
}
