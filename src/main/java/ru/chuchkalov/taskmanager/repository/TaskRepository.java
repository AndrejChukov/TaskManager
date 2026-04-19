package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.repository.CrudRepository;
import ru.chuchkalov.taskmanager.entity.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByUserId(Long id);
}
