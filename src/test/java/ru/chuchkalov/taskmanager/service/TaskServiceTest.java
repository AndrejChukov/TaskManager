package ru.chuchkalov.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.chuchkalov.taskmanager.dto.projection.TaskProjection;
import ru.chuchkalov.taskmanager.dto.request.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.AccessDeniedException;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.TaskMapper;
import ru.chuchkalov.taskmanager.repository.TaskRepository;
import ru.chuchkalov.taskmanager.repository.UserRepository;
import ru.chuchkalov.taskmanager.service.TaskService;

import java.time.Instant;
import java.util.*;

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

        taskResponseDTO = new TaskResponseDTO(
                "title", "sfd", null, null, null, null);

        taskRequestDTO = new TaskRequestDTO(
                null, "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM
        );
    }


    @Test
    public void createTask_Success() {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(
                "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM,
                Instant.now(), mockUser.getUsername()
        );

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.title());

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
    public void createTask_ThrowException_UserNotFound() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("Andrey");

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                null, "Title", "Descr",
                Task.Status.NEW, Task.Priority.MEDIUM
        );

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.title());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(taskFromMapper);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.createTask(taskRequestDTO, 2L));

        assertEquals("User with ID 2 not found", exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));

    }

    @Test
    public void createTaskToCurrentUser_Success() {
        Task task = new Task();
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(USER_ID);

        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(task);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(taskMapper.convert(task)).thenReturn(taskResponseDTO);

        TaskResponseDTO response = taskService.createTaskToCurrentUser(taskRequestDTO);

        assertNotNull(response);
        assertEquals(taskResponseDTO, response);

        verify(taskMapper, times(1)).toEntity(taskRequestDTO);
        verify(taskMapper, times(1)).convert(task);
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void getTasksByUserId_Success() {
        TaskProjection mockProjection = mock(TaskProjection.class);

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(taskRepository.findTasksResponseDtoByUserId(USER_ID)).thenReturn(List.of(mockProjection));
        when(taskMapper.convert(mockProjection)).thenReturn(taskResponseDTO);

        List<TaskResponseDTO> response = taskService.getTasksByUserId(USER_ID);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(taskResponseDTO, response.get(0));

        verify(userRepository, times(1)).existsById(USER_ID);
        verify(taskRepository, times(1)).findTasksResponseDtoByUserId(USER_ID);
        verify(taskMapper, times(1)).convert(mockProjection);
    }

    @Test
    public void getTaskByUserId_ThrowException_EntityNotFound_UserIsEmpty() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> taskService.getTasksByUserId(USER_ID));
    }

    @Test
    public void getTaskByUserId_ThrowException_EntityNotFound_TasksIsEmpty() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(taskRepository.findTasksResponseDtoByUserId(USER_ID)).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class, () -> taskService.getTasksByUserId(USER_ID));
    }

    @Test
    public void getTasksFromCurrentUser_Success() {
        Task task = new Task();
        TaskProjection mockProjection = mock(TaskProjection.class);
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(USER_ID);

        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(taskRepository.findTasksResponseDtoByUserId(USER_ID)).thenReturn(List.of(mockProjection));
        when(taskMapper.convert(mockProjection)).thenReturn(taskResponseDTO);

        List<TaskResponseDTO> response = taskService.getTasksFromCurrentUser();

        assertNotNull(response);
        assertEquals(taskResponseDTO, response.get(0));
        assertEquals(1, response.size());

        verify(taskRepository, times(1)).findTasksResponseDtoByUserId(USER_ID);
        verify(taskMapper, times(1)).convert(mockProjection);

    }

    @Test
    public void getAllTasks_Success() {
        Task mockTask = mock(Task.class);

        List<Task> tasks = Collections.singletonList(mockTask);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> mockPage = new PageImpl<>(tasks, pageable, 1);

        when(taskRepository.findAll(pageable)).thenReturn(mockPage);
        when(taskMapper.convert(mockTask)).thenReturn(taskResponseDTO);

        Page<TaskResponseDTO> response = taskService.getAllTasks(pageable);

        assertNotNull(response);
        assertEquals(taskResponseDTO, response.getContent().get(0));
        assertEquals(1, response.getContent().size());

        verify(taskRepository, times(1)).findAll(pageable);
        verify(taskMapper, times(1)).convert(mockTask);
    }

    @Test
    public void updateTask_Success() {
        Task oldTask = new Task();
        oldTask.setTitle("Old title");

        Task taskFromMapper = new Task();
        taskFromMapper.setTitle(taskRequestDTO.title());

        when(taskMapper.toEntity(taskRequestDTO)).thenReturn(taskFromMapper);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(oldTask));
        when(taskRepository.save(taskFromMapper)).thenReturn(taskFromMapper);

        taskService.updateTask(taskRequestDTO, mockUser.getId());

        verify(taskRepository).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskRequestDTO.title(), capturedTask.getTitle());
        assertNotNull(capturedTask.getCreatedAt());
    }

    @Test
    public void deleteTask_Success() {
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
    public void deleteTask_ThrowException_UserIsNotOwner() {
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