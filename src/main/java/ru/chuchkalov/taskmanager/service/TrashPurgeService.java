package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.repository.TaskRepository;

import java.util.Date;

@Service
@AllArgsConstructor
public class TrashPurgeService {

    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeOldTrash() {
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
        taskRepository.hardDeleteTasksOlderThan(thirtyDaysAgo);
    }
}
