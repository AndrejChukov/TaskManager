package ru.chuchkalov.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;


@Data
@Entity
@Table(name = "tasks")
@SQLDelete(sql = "UPDATE tasks SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;

    private boolean deleted = false;
    private Instant deletedAt;

    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Priority priority;

    private Instant createdAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public enum Status {
        NEW, IN_PROGRESS, DONE
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
