package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.repository.TaskRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class TrashPurgeService {

    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeOldTrash() {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        taskRepository.hardDeleteTasksOlderThan(thirtyDaysAgo);
    }
}
