package ru.chuchkalov.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 2, max = 50)
    private String username;
    @NotBlank
    @Size(min = 2, max = 100)
    private String password;
    @Email
    private String email;
    @Enumerated(EnumType.STRING)
    @NotBlank
    private Role role;

    public enum Role {
        ADMIN, USER
    }
}
