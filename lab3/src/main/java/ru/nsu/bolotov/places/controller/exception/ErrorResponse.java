package ru.nsu.bolotov.places.controller.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String message, LocalDateTime time) {
}
