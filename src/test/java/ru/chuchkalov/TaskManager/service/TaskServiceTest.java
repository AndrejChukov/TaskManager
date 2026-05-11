package ru.chuchkalov.TaskManager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.chuchkalov.taskmanager.dto.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.AccessDeniedException;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.TaskMapper;
import ru.chuchkalov.taskmanager.repository.TaskRepository;
import ru.chuchkalov.taskmanager.repository.UserRepository;
import ru.chuchkalov.taskmanager.service.TaskService;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @Mock TaskMapper taskMapper;

    @InjectMocks TaskService taskService;

    @Captor ArgumentCaptor<Task> taskCaptor;

    private final Long USER_ID = 1L;
    private TaskRequestDTO taskRequestDTO;
    private TaskResponseDTO taskResponseDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setUsername("Andrey");

        taskRequestDTO = new TaskRequestDTO(
                null, "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM
        );
    }


    @Test
    public void verifySetCreatedAtCalled() {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(
                null, "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM,
                new Date(), mockUser.getUsername()
        );

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.getTitle());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(taskFromMapper);
        when(taskMapper.convert(taskFromMapper)).thenReturn(taskResponseDTO);

        taskService.createTask(taskRequestDTO, mockUser.getId());

        verify(taskRepository).save(taskCaptor.capture());
        Task taskFromCaptor = taskCaptor.getValue();

        assertEquals("Title", taskFromCaptor.getTitle());
        assertEquals(mockUser, taskFromCaptor.getUser());
        assertNotNull(taskFromCaptor.getCreatedAt());
        assertEquals(Task.Status.NEW, taskFromCaptor.getStatus());

    }

    @Test
    public void createTaskShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Andrey");

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                null, "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM
        );

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.getTitle());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(taskFromMapper);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.createTask(taskRequestDTO, 2L));

        assertEquals("User with ID 2 not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));

    }

    @Test
    public void verifyUpdatingTask() {
        Task oldTask = new Task();
        oldTask.setTitle("Old title");

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.getTitle());

        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(taskFromMapper);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(oldTask));
        when(taskRepository.save(taskFromMapper)).thenReturn(taskFromMapper);

        taskService.updateTask(taskRequestDTO, mockUser.getId());

        verify(taskRepository).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskRequestDTO.getTitle(), capturedTask.getTitle());
        assertNotNull(capturedTask.getCreatedAt());
    }

    @Test
    public void verifyDeletingTaskSuccess() {
        Task mockTask = new Task();
        mockTask.setTitle("Test title");
        mockTask.setUser(mockUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getName()).thenReturn(mockUser.getUsername());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(mockTask));

        taskService.deleteTask(10L);

        verify(taskRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void deleteTaskShouldThrowExceptionWhenUserIsNotOwner() {
        Task mockTask = new Task();
        mockTask.setTitle("Test title");
        mockTask.setUser(mockUser);

        User otherMockUser = new User();
        otherMockUser.setUsername("Other User");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getName()).thenReturn(otherMockUser.getUsername());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(mockTask));

        assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(10L));

        verify(taskRepository, never()).deleteById(anyLong());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

}