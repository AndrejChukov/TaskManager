package ru.chuchkalov.taskmanager.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.repository.TaskRepository;
import ru.chuchkalov.taskmanager.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public TaskService(TaskRepository taskService, UserRepository userRepository) {
        this.taskRepository = taskService;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponseDTO createTask(Task task, Long id) {
        Optional<User> currentUser = userRepository.findById(id);
        if (currentUser.isPresent()) {
            task.setUser(currentUser.get());
            task.setCreatedAt(new Date());
            taskRepository.save(task);
            return convertTaskToDTO(task);
        }
        throw new ResponseStatusException(HttpStatusCode.valueOf(404));
    }

    public List<Task> findTasksByUserId(Long id) {
        return taskRepository.findByUserId(id);
    }

    @Transactional
    public void updateTask(Task newTask, Long id) {
        taskRepository.findById(id)
                .map(t -> {
                    newTask.setId(t.getId());
                    return taskRepository.save(newTask);
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public TaskResponseDTO convertTaskToDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUser().getUsername()
        );
    }

}
