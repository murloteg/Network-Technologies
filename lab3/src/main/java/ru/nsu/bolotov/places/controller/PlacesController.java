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
import ru.nsu.bolotov.places.model.dto.PlaceNameDto;
import ru.nsu.bolotov.places.model.dto.geocode.GeoCodeResponseDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceDescriptionResponseDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.InterestingPlacesDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceOpenTripMapDto;
import ru.nsu.bolotov.places.service.PlacesService;

import java.io.IOException;

@RestController
@RequestMapping(value = "/places", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlacesController {
    private final PlacesService placesService;

    @GetMapping
    public ResponseEntity<GeoCodeResponseDto> getLocationsByPlace(@RequestBody PlaceNameDto placeNameDto) throws IOException {
        GeoCodeResponseDto locations = placesService.findLocationsByPlaceName(placeNameDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(locations);
    }

    @GetMapping(value = "/en/interesting/list")
    public ResponseEntity<InterestingPlacesDto> getForeignInterestingPlacesByLocationPoint(
            @RequestBody CommonLocationPointDto commonLocationPointDto
    ) throws IOException {
        InterestingPlacesDto interestingPlacesDto = placesService.findInterestingPlacesByLocationPoint(commonLocationPointDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(interestingPlacesDto);
    }

    @GetMapping(value = "/en/interesting/description")
    public ResponseEntity<PlaceDescriptionResponseDto> getDetailedDescriptionForForeignPlace(
            @RequestBody PlaceOpenTripMapDto placeOpenTripMapDto
    ) throws IOException {
        PlaceDescriptionResponseDto placeDescriptionDto = placesService.findDescriptionForPlaceByPlaceId(placeOpenTripMapDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeDescriptionDto);
    }
}
