package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.dto.projection.TaskProjection;
import ru.chuchkalov.taskmanager.dto.request.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.exception.AccessDeniedException;
import ru.chuchkalov.taskmanager.mapper.TaskMapper;
import ru.chuchkalov.taskmanager.repository.TaskRepository;
import ru.chuchkalov.taskmanager.repository.UserRepository;

import java.time.Instant;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto, Long id) {
        Task task = taskMapper.toEntity(dto);
        task.setStatus(Task.Status.NEW);

        return userRepository.findById(id)
                .map(u -> {
                    task.setUser(u);
                    task.setCreatedAt(Instant.now());
                    taskRepository.save(task);
                    log.info("Task {} created successfully for user {}", task.getId(), u.getId());
                    return taskMapper.convert(task);
                }).orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional
    public TaskResponseDTO createTaskToCurrentUser(TaskRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long id = jwt.getClaim("id");;
        Task task = taskMapper.toEntity(dto);
        return userRepository.findById(id)
                        .map(u -> {
                            task.setUser(u);
                            task.setCreatedAt(Instant.now());
                            taskRepository.save(task);
                            log.info("Task {} created successfully for user {}", task.getId(), id);
                            return taskMapper.convert(task);
                        }).orElseThrow(() -> new EntityNotFoundException("Error has occurred with user's id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByUserId(Long id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User with ID " + id + " not found");

        List<TaskProjection> projections = taskRepository.findTasksResponseDtoByUserId(id);

        if (projections.isEmpty())
            throw new EntityNotFoundException("Tasks with User's id " + id + " not found");

        return projections.stream()
                .map(taskMapper::convert)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksFromCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long id = jwt.getClaim("id");

        return taskRepository.findTasksResponseDtoByUserId(id).stream()
                .map(taskMapper::convert)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::convert);
    }

    @Transactional
    public void updateTask(TaskRequestDTO dto, Long id) {
        Task newTask = taskMapper.toEntity(dto);
        taskRepository.findById(id)
                .map(t -> {
                    newTask.setId(t.getId());
                    newTask.setUser(t.getUser());
                    newTask.setCreatedAt(t.getCreatedAt());
                    log.info("Task {} updated successfully for user {}",
                            t.getId(), t.getUser().getId());
                    return taskRepository.save(newTask);
                })
                .orElseThrow(() ->
                        new EntityNotFoundException("Task with ID " + id + " not found"));
    }

    @Transactional
    public void deleteTask(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Task with ID " + id + " not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = task.getUser().getUsername().equals(auth.getName());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You cannot delete task with ID " + task.getId() + ", " + isAdmin + ", " + isOwner);
        }
        taskRepository.deleteById(id);
        log.info("Task {} deleted successfully for user {}", id, task.getUser().getId());
    }

}
