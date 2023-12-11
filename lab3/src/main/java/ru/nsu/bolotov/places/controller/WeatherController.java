package ru.nsu.bolotov.places.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.bolotov.places.model.dto.CommonLocationPointDto;
import ru.nsu.bolotov.places.model.dto.weather.WeatherResponseDto;
import ru.nsu.bolotov.places.service.WeatherService;

import java.io.IOException;

@RestController
@RequestMapping(value = "/places/weather", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponseDto> getWeatherByCoords(
            @RequestBody CommonLocationPointDto commonLocationPointDto) throws IOException {
        WeatherResponseDto weatherResponse = weatherService.findWeatherByCoords(commonLocationPointDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(weatherResponse);
    }
}
