package ru.chuchkalov.taskmanager.dto.response;

import ru.chuchkalov.taskmanager.entity.User;

public record UserResponseDTO(
        String username,
        String email,
        User.Role role
) {}
