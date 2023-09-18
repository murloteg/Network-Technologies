package ru.nsu.bolotov.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationEntity {
    private final UUID uuid;
    private final LocalDateTime date;

    public ApplicationEntity() {
        this.uuid = UUID.randomUUID();
        this.date = LocalDateTime.now();
    }

    public UUID getUUID() {
        return uuid;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
