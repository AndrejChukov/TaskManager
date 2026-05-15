package ru.chuchkalov.taskmanager.dto.response;

import ru.chuchkalov.taskmanager.entity.User;

public record LoginResponseDTO(String token, String name, String email, User.Role role) {
}
