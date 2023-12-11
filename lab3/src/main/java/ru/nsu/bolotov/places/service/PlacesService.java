package ru.nsu.bolotov.places.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.bolotov.places.model.dto.CommonLocationPointDto;
import ru.nsu.bolotov.places.model.dto.geocode.GeoCodeResponseDto;
import ru.nsu.bolotov.places.model.dto.PlaceNameDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceDescriptionResponseDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.InterestingPlacesDto;
import ru.nsu.bolotov.places.model.dto.opentripmap.PlaceOpenTripMapDto;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlacesService {
    @Value(value = "${geocode.dev.api.key}")
    private String geocodeApiKey;

    @Value(value = "${opentripmap.dev.api.key}")
    private String openTripMapApiKey;

    @Value(value = "${opentripmap.max.radius.from.location.point.in.meters}")
    private String radius;

    public GeoCodeResponseDto findLocationsByPlaceName(PlaceNameDto placeNameDto) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?q=" + placeNameDto.placeName() + "&locale=ru&key=" + geocodeApiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (Objects.isNull(response.body())) {
                throw new IllegalArgumentException();
            }
            log.info("Successful response for place: \"{}\"", placeNameDto.placeName());

            String responseAsJson = response.body().string();
            log.info(responseAsJson);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseAsJson, GeoCodeResponseDto.class);
        }
    }

    public InterestingPlacesDto findInterestingPlacesByLocationPoint(CommonLocationPointDto commonLocationPointDto) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/en/places/radius?radius=" + radius + "&lon=" + commonLocationPointDto.longitude() + "&lat=" + commonLocationPointDto.latitude() + "&apikey=" + openTripMapApiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (Objects.isNull(response.body())) {
                throw new IllegalArgumentException();
            }
            log.info("Successful response for points: (lat: {} ; lon: {})", commonLocationPointDto.latitude(), commonLocationPointDto.longitude());

            String responseAsJson = response.body().string();
            log.info(responseAsJson);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseAsJson, InterestingPlacesDto.class);
        }
    }

    public PlaceDescriptionResponseDto findDescriptionForPlaceByPlaceId(PlaceOpenTripMapDto placeOpenTripMapDto) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/en/places/xid/" + placeOpenTripMapDto.placeId() + "?apikey=" + openTripMapApiKey)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (Objects.isNull(response.body())) {
                throw new IllegalArgumentException();
            }
            log.info("Successful response for place with id: {}", placeOpenTripMapDto.placeId());

            String responseAsJson = response.body().string();
            log.info(responseAsJson);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseAsJson, PlaceDescriptionResponseDto.class);
        }
    }
}
