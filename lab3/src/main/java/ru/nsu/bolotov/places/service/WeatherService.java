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
import ru.nsu.bolotov.places.model.dto.weather.WeatherResponseDto;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value(value = "${weather.dev.api.key}")
    private String weatherApiKey;

    public WeatherResponseDto findWeatherByCoords(CommonLocationPointDto commonLocationPointDto) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat=" + commonLocationPointDto.latitude() + "&lon=" + commonLocationPointDto.longitude() + "&appid=" + weatherApiKey)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (Objects.isNull(response.body())) {
                throw new IllegalArgumentException();
            }
            log.info("Successful response for points: (lat: {} ; lon: {})", commonLocationPointDto.latitude(), commonLocationPointDto.longitude());

            String responseAsJson = response.body().string();
            log.info(responseAsJson);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseAsJson, WeatherResponseDto.class);
        }
    }
}
