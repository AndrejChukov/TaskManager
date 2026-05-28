package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.dto.projection.TaskProjection;
import ru.chuchkalov.taskmanager.dto.response.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    Page<Task> findAll(Pageable pageable);

    @Query("SELECT t.title AS title, t.description AS description, t.status AS status, " +
            "t.priority AS priority, t.createdAt AS createdAt, u.username AS userUsername " +
            "FROM Task t JOIN t.user u WHERE u.id = :id")
    List<TaskProjection> findTasksResponseDtoByUserId(@Param("id") Long userId);

    @Modifying
    @Query(value = "DELETE FROM tasks WHERE deleted = true AND deleted_at < :date", nativeQuery = true)
    void hardDeleteTasksOlderThan(@Param("date") Instant date);
}
