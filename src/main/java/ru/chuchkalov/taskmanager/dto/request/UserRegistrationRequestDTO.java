package ru.chuchkalov.taskmanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDTO(
        @NotBlank
        @Size(min = 2, max = 50)
        String username,
        @NotBlank
        @Size(min = 2, max = 255)
        String password,
        @Email
        @Size(max = 50)
        String email
) {
}
