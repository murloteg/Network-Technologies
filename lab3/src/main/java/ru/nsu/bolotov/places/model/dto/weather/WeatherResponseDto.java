package ru.nsu.bolotov.places.model.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.nsu.bolotov.places.model.dto.CommonLocationPointDto;

import java.util.List;

@JsonIgnoreProperties(value = {"sys", "id", "cod", "base"})
public record WeatherResponseDto(
        @JsonProperty(value = "coord")
        CommonLocationPointDto locationPointDto,
        @JsonProperty(value = "weather")
        List<WeatherInfoDto> weatherInfoDto,
        @JsonProperty(value = "main")
        TemperatureInfoDto temperatureInfoDto,
        @JsonProperty(value = "visibility")
        int visibility,
        @JsonProperty(value = "wind")
        WindInfoDto windInfoDto,
        @JsonProperty(value = "rain")
        RainInfoDto rainInfoDto,
        @JsonProperty(value = "clouds")
        CloudsInfoDto cloudsInfoDto,
        @JsonIgnore
        @JsonProperty(value = "dt")
        int timeOfCalculation,
        @JsonProperty(value = "timezone")
        int timezone,
        @JsonProperty(value = "name")
        String cityName
) {
}
