package ru.chuchkalov.taskmanager.dto.response;

import ru.chuchkalov.taskmanager.entity.Task;
import java.util.Date;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Task.Status status,
        Task.Priority priority,
        Date createdAt,
        String ownerName) {
}
