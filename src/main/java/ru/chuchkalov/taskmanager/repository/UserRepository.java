package ru.chuchkalov.taskmanager.repository;

import org.springframework.data.repository.CrudRepository;
import ru.chuchkalov.taskmanager.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
}