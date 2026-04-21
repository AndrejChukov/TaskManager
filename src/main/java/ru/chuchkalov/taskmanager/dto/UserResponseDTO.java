package ru.chuchkalov.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.chuchkalov.taskmanager.entity.User;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private User.Role role;
}
