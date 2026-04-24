package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.chuchkalov.taskmanager.dto.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.UserMapper;
import ru.chuchkalov.taskmanager.repository.UserRepository;
import ru.chuchkalov.taskmanager.security.SecurityConfig;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityConfig securityConfig;

    public UserResponseDTO createUser(User user) {
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return userMapper.convert(user);
    }

    public Page<UserResponseDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::convert);
    }

    public UserResponseDTO getUser(Long id) {
        return userRepository.findById(id)
                .map(userMapper::convert)
                .orElseThrow(
                () -> new EntityNotFoundException("User with ID " + id + " not found")
        );
    }
}
