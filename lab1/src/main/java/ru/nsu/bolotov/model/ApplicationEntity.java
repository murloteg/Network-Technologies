package ru.nsu.bolotov.model;

import java.util.UUID;

public class ApplicationEntity {
    private final UUID uuid;

    public ApplicationEntity() {
        this.uuid = UUID.randomUUID();
    }

    public ApplicationEntity(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
