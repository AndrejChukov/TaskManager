package ru.chuchkalov.taskmanager.dto.response;

import ru.chuchkalov.taskmanager.entity.Task;

import java.time.Instant;

public record TaskResponseDTO(
        String title,
        String description,
        Task.Status status,
        Task.Priority priority,
        Instant createdAt,
        String ownerName) {
}
