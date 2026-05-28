package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.chuchkalov.taskmanager.dto.projection.UserProjection;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u.username AS username, u.email AS email, u.role AS role " +
            "FROM User u WHERE u.id = :id")
    Optional<UserProjection> findUserProjectionById(@Param("id") Long id);

    @Query("SELECT u.username AS username, u.email AS email, u.role AS role " +
            "FROM User u")
    Page<UserProjection> findAllUserProjections(Pageable pageable);
}