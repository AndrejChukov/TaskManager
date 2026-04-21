package ru.chuchkalov.taskmanager.dto;

import lombok.Data;
import ru.chuchkalov.taskmanager.entity.User;

@Data
public class UserResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final User.Role role;
}
