package ru.chuchkalov.taskmanager.service;

import org.springframework.stereotype.Service;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        // TODO: Validation
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }
}
