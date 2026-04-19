package ru.chuchkalov.taskmanager.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    public Task createTask(Task task, Long id) {
        Optional<User> currentUser = userRepository.findById(id);
        if (currentUser.isPresent()) {
            task.setUser(currentUser.get());
            task.setCreatedAt(new Date());
            return taskRepository.save(task);
        }
        throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
    }

    public List<Task> findTasksByUserId(Long id) {
        return taskRepository.findByUserId(id);
    }

    public void updateTask(Task newUser, Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            taskRepository.save(newUser);
        }
        throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

}
