package ru.chuchkalov.taskmanager.dto.response;

import ru.chuchkalov.taskmanager.entity.User;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        User.Role role
) {}
