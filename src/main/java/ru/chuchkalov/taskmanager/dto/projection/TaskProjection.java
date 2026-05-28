package ru.chuchkalov.taskmanager.dto.projection;

import ru.chuchkalov.taskmanager.entity.Task;

import java.time.Instant;

public interface TaskProjection {
    String getTitle();
    String getDescription();
    Task.Status getStatus();
    Task.Priority getPriority();
    Instant getCreatedAt();
    String getUserUsername();
}
