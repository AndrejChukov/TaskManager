package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chuchkalov.taskmanager.dto.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.UserMapper;
import ru.chuchkalov.taskmanager.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(User user) {
        // TODO: Validation
        userRepository.save(user);
        return userMapper.convert(user);
    }

    public List<UserResponseDTO> getUsers() {
        return ((List<User>) userRepository.findAll()).stream()
                .map(userMapper::convert)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUser(Long id) {
        return userRepository.findById(id)
                .map(userMapper::convert)
                .orElseThrow(
                () -> new EntityNotFoundException("User with ID " + id + " not found")
        );
    }
}
