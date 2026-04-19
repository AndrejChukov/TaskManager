package ru.chuchkalov.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;

import java.util.Date;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
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

    public TaskResponseDTO convert() {
        return new TaskResponseDTO(id, title, description, status, priority, createdAt, user.getUsername());
    }

}
