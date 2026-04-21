package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.TaskMapper;
import ru.chuchkalov.taskmanager.repository.TaskRepository;
import ru.chuchkalov.taskmanager.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponseDTO createTask(Task task, Long id) {
        return userRepository.findById(id)
                .map(u -> {
                    task.setUser(u);
                    task.setCreatedAt(new Date());
                    taskRepository.save(task);
                    return taskMapper.convert(task);
                }).orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    public List<TaskResponseDTO> findTasksByUserId(Long id) {
        List<Task> tasks = taskRepository.findByUserId(id);
        userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with ID " + id + " not found"));

        if (tasks.isEmpty()) throw new EntityNotFoundException("Tasks with User's id " + id + " not found");

        return tasks.stream()
                .map(taskMapper::convert)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getTasks() {
        return ((List<Task>) taskRepository.findAll()).stream()
                .map(taskMapper::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTask(Task newTask, Long id) {
        taskRepository.findById(id)
                .map(t -> {
                    newTask.setId(t.getId());
                    newTask.setUser(t.getUser());
                    newTask.setCreatedAt(t.getCreatedAt());
                    return taskRepository.save(newTask);
                })
                .orElseThrow(() ->
                        new EntityNotFoundException("Task with ID " + id + " not found"));
    }

    public void deleteTask(Long id) {
        taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with ID " + id + " not found"));
        taskRepository.deleteById(id);
    }

}
