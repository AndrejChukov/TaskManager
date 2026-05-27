package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.request.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.TaskResponseDTO;
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
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponseDTO createTaskByUserId(@RequestBody @Valid TaskRequestDTO dto, @PathVariable("id") Long id) {
        return taskService.createTask(dto, id);
    }

    @PostMapping("/my-tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTaskToCurrentUser(@RequestBody @Valid TaskRequestDTO dto) {
        return taskService.createTaskToCurrentUser(dto);
    }

    @GetMapping("/tasks/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDTO> getTaskByUserId(@PathVariable("id") Long id) {
        return taskService.getTasksByUserId(id);
    }

    @GetMapping("/my-tasks")
    public List<TaskResponseDTO> getTaskFromCurrentUser() {
        return taskService.getTasksFromCurrentUser();
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponseDTO> getTasks(
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return taskService.getTasks(pageable);
    }

    @PutMapping("/my-tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTask(@RequestBody @Valid TaskRequestDTO newTask, @PathVariable("id") Long id) {
        taskService.updateTask(newTask, id);
    }

    @DeleteMapping("/my-tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
    }

}
