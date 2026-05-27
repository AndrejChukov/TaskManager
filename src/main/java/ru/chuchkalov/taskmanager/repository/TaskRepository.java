package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.entity.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long id);

    @Override
    @EntityGraph(attributePaths = "user")
    Page<Task> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tasks WHERE delete = true AND deleted_at < :date", nativeQuery = true)
    void hardDeleteTasksOlderThan(@Param("date") Date date);
}
