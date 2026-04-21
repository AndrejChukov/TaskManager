package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.service.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@RequestBody @Valid Task task, @PathVariable("id") Long id) {
        return taskService.createTask(task, id);
    }

    @GetMapping("/tasks/user/{id}")
    public List<TaskResponseDTO> getTaskByUser(@PathVariable("id") Long id) {
        return taskService.findTasksByUserId(id);
    }

    @GetMapping("/tasks")
    public List<TaskResponseDTO> getTasks() {
        return taskService.getTasks();
    }

    @PutMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String updateTask(@RequestBody @Valid Task newTask, @PathVariable("id") Long id) {
        taskService.updateTask(newTask, id);
        return "{\"status\": \"accepted\"}";
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return "{\"status\": \"accepted\"}";
    }

}
