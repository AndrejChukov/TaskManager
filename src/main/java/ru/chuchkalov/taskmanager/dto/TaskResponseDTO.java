package ru.chuchkalov.taskmanager.dto;
import lombok.Data;
import ru.chuchkalov.taskmanager.entity.Task;

import java.util.Date;

@Data
public class TaskResponseDTO {
    private final Long id;
    private final String title;
    private final String description;
    private final Task.Status status;
    private final Task.Priority priority;
    private final Date createdAt;
    private final String ownerName;
}
