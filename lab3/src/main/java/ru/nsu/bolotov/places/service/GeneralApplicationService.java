package ru.nsu.bolotov.places.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nsu.bolotov.places.exception.AsyncExecutionException;
import ru.nsu.bolotov.places.model.dto.CommonLocationPointDto;
import ru.nsu.bolotov.places.model.dto.PlaceNameDto;
import ru.nsu.bolotov.places.model.dto.PlaceResponseDto;
import ru.nsu.bolotov.places.model.dto.geocode.GeoCodeResponseDto;
import ru.nsu.bolotov.places.model.dto.geocode.GeocodeLocationPointDto;
import ru.nsu.bolotov.places.model.dto.geocode.LocationHitDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.FeatureDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.InterestingPlacesDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceDescriptionResponseDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceOpenTripMapDto;
import ru.nsu.bolotov.places.model.dto.weather.WeatherResponseDto;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class GeneralApplicationService {
    private final PlacesService placesService;
    private final WeatherService weatherService;

    public PlaceResponseDto getPlaceInfoByPlaceName(PlaceNameDto placeNameDto) throws IOException {
        GeoCodeResponseDto locations = placesService.findLocationsByPlaceName(placeNameDto);
        LocationHitDto firstLocation = locations.hits().stream().findFirst().orElseThrow(IllegalArgumentException::new);
        GeocodeLocationPointDto geocodeLocationPointDto = firstLocation.geocodeLocationPointDto();
        CommonLocationPointDto commonLocationPointDto = new CommonLocationPointDto(
                geocodeLocationPointDto.latitude(),
                geocodeLocationPointDto.longitude()
        );

        CompletableFuture<WeatherResponseDto> completableFutureForWeather = new CompletableFuture<>();
        completableFutureForWeather.completeAsync(() -> {
            WeatherResponseDto weatherResponseDto;
            try {
                weatherResponseDto = weatherService.findWeatherByCoords(commonLocationPointDto);
            } catch (IOException exception) {
                throw new IllegalStateException(exception);
            }
            return weatherResponseDto;
        });

        CompletableFuture<InterestingPlacesDto> completableFutureForInterestingPlaces = new CompletableFuture<>();
        completableFutureForInterestingPlaces.completeAsync(() -> {
            InterestingPlacesDto interestingPlacesDto;
            try {
                interestingPlacesDto = placesService.findInterestingPlacesByLocationPoint(commonLocationPointDto);
            } catch (IOException exception) {
                throw new IllegalStateException(exception);
            }
            return interestingPlacesDto;
        });

        WeatherResponseDto weatherResponseDto;
        InterestingPlacesDto interestingPlacesDto;
        try {
            weatherResponseDto = completableFutureForWeather.get();
            interestingPlacesDto = completableFutureForInterestingPlaces.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AsyncExecutionException();
        } catch (ExecutionException exception) {
            throw new AsyncExecutionException();
        }

        FeatureDto placeFeatureDto = interestingPlacesDto.features().stream().findFirst().orElseThrow(IllegalArgumentException::new);
        PlaceOpenTripMapDto placeOpenTripMapDto = new PlaceOpenTripMapDto(placeFeatureDto.propertyDto().placeId());
        PlaceDescriptionResponseDto placeDescriptionResponseDto = placesService.findDescriptionForPlaceByPlaceId(placeOpenTripMapDto);
        return new PlaceResponseDto(
                weatherResponseDto,
                interestingPlacesDto,
                placeDescriptionResponseDto
        );
    }
}
