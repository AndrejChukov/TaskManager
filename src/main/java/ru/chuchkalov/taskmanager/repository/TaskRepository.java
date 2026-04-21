package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chuchkalov.taskmanager.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long id);
}
