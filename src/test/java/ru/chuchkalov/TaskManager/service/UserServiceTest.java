package ru.chuchkalov.TaskManager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.UserMapper;
import ru.chuchkalov.taskmanager.repository.UserRepository;
import ru.chuchkalov.taskmanager.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @Captor ArgumentCaptor<User> userCaptor;

    private User mockUser;
    private UserResponseDTO userDto;
    private final Long USER_ID = 1L;

    @BeforeEach
    void init() {
        mockUser = new User();
        mockUser.setUsername("Test user");
        mockUser.setId(USER_ID);
        mockUser.setPassword("1234");

        userDto = new UserResponseDTO(
                mockUser.getId(), mockUser.getUsername(),
                mockUser.getEmail(), mockUser.getRole());
    }

    @Test
    public void createUserTest() {
        when(passwordEncoder.encode(mockUser.getPassword()))
                .thenReturn("hashed_password");

        userService.createUser(mockUser);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getId());
        assertEquals("hashed_password", capturedUser.getPassword());
    }

    @Test
    public void getUsersTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = new PageImpl<>(List.of(mockUser));
        when(userRepository.findAll(pageable)).thenReturn(users);
        when(userMapper.convert(mockUser)).thenReturn(userDto);

        Page<UserResponseDTO> responseUsers = userService.getUsers(pageable);

        assertEquals(1, responseUsers.getTotalElements());
        assertEquals(userDto.username(), responseUsers.getContent().get(0).username());

        verify(userRepository).findAll(pageable);

    }

    @Test
    public void getUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userMapper.convert(mockUser)).thenReturn(userDto);

        UserResponseDTO response = userService.getUser(USER_ID);

        assertEquals(userDto.id(), response.id());
        assertEquals(userDto.username(), response.username());
        assertEquals(userDto.email(), response.email());
        assertEquals(userDto.role(), response.role());

        verify(userRepository).findById(anyLong());
    }

    @Test
    public void getUserTestShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(USER_ID));

        verify(userRepository).findById(anyLong());
        verify(userMapper, never()).convert(any());
    }

}