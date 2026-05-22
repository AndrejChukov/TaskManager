package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.chuchkalov.taskmanager.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long id);

    @Override
    @Query(value = "SELECT task FROM Task task JOIN FETCH task.user",
            countQuery = "SELECT count(task) FROM Task task")
    Page<Task> findAll(Pageable pageable);
}
