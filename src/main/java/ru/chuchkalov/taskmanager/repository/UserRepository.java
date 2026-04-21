package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chuchkalov.taskmanager.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}