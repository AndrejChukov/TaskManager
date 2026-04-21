package ru.chuchkalov.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String title;
    @Size(max = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Priority priority;
    private Date createdAt = new Date();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum Status {
        NEW, IN_PROGRESS, DONE
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
