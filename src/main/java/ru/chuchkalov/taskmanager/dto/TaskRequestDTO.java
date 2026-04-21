package ru.chuchkalov.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.chuchkalov.taskmanager.entity.Task;

@Data
@AllArgsConstructor
public class TaskRequestDTO {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
}
