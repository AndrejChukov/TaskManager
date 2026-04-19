package ru.chuchkalov.taskmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.service.TaskService;

import java.util.ArrayList;
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
    public Task createTask(@RequestBody Task task, @PathVariable("id") Long id) {
        return taskService.createTask(task, id);
    }

    @GetMapping("/tasks/user/{id}")
    public List<TaskResponseDTO> getUser(@PathVariable("id") Long id) {
        List<Task> tasks = taskService.findTasksByUserId(id);
        List<TaskResponseDTO> result = new ArrayList<>();
        for (Task task : tasks) {
            result.add(task.convert());
        }
        return result;
    }

    @PutMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateTask(@RequestBody Task newTask, @PathVariable("id") Long id) {
        taskService.updateTask(newTask, id);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
    }

}
