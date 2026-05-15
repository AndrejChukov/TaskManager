package ru.chuchkalov.taskmanager.dto.request;

import ru.chuchkalov.taskmanager.entity.Task;

public record TaskRequestDTO(
        Long id,
        String title,
        String description,
        Task.Status status,
        Task.Priority priority) {
}
