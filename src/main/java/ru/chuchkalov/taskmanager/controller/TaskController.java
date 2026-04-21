package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@RequestBody @Valid TaskRequestDTO dto, @PathVariable("id") Long id) {
        return taskService.createTask(dto, id);
    }

    @GetMapping("/tasks/user/{id}")
    public List<TaskResponseDTO> getTaskByUser(@PathVariable("id") Long id) {
        return taskService.findTasksByUserId(id);
    }

    @GetMapping("/tasks")
    public Page<TaskResponseDTO> getTasks(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @PutMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTask(@RequestBody @Valid TaskRequestDTO newTask, @PathVariable("id") Long id) {
        taskService.updateTask(newTask, id);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
    }

}
