package ru.nsu.bolotov.places.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.bolotov.places.model.dto.PlaceNameDto;
import ru.nsu.bolotov.places.model.dto.PlaceResponseDto;
import ru.nsu.bolotov.places.service.GeneralApplicationService;

import java.io.IOException;

@RestController
@RequestMapping(value = "/places/info", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GeneralApplicationController {
    private final GeneralApplicationService generalApplicationService;

    @GetMapping
    public ResponseEntity<PlaceResponseDto> getAllPlaceInfoByPlaceName(
            @RequestBody PlaceNameDto placeNameDto
    ) throws IOException {
        PlaceResponseDto placeInfoDto = generalApplicationService.getPlaceInfoByPlaceName(placeNameDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeInfoDto);
    }
}
