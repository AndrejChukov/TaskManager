package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chuchkalov.taskmanager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}