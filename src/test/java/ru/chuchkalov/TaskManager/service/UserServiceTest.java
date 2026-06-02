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
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.chuchkalov.taskmanager.dto.projection.UserProjection;
import ru.chuchkalov.taskmanager.dto.request.LoginRequestDTO;
import ru.chuchkalov.taskmanager.dto.request.UserRegistrationRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.LoginResponseDTO;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.UserMapper;
import ru.chuchkalov.taskmanager.repository.UserRepository;
import ru.chuchkalov.taskmanager.service.TokenService;
import ru.chuchkalov.taskmanager.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock TokenService tokenService;

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
                mockUser.getUsername(),
                mockUser.getEmail(), mockUser.getRole());
    }

    @Test
    public void registerTest() {
        UserRegistrationRequestDTO requestDto = mock(UserRegistrationRequestDTO.class);
        when(userMapper.registerToEntity(requestDto)).thenReturn(mockUser);
        when(passwordEncoder.encode(mockUser.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.convert(mockUser)).thenReturn(userDto);

        UserResponseDTO result = userService.register(requestDto);

        assertNotNull(result);
        assertEquals("hashed_password", mockUser.getPassword());

        verify(userMapper, times(1)).registerToEntity(requestDto);
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(mockUser);
        verify(userMapper, times(1)).convert(mockUser);
    }

    @Test
    public void authenticateTest() {
        User fakeUser = mock(User.class);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("my-user", "my-password");
        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(fakeUser);

        when(tokenService.generateToken(mockAuth)).thenReturn("fake-token-123241231");

        when(fakeUser.getUsername()).thenReturn("my-user");
        when(fakeUser.getEmail()).thenReturn("my-email");
        when(fakeUser.getRole()).thenReturn(User.Role.USER);

        LoginResponseDTO response = userService.authenticate(loginRequestDTO);

        assertNotNull(response);
        assertEquals("fake-token-123241231", response.token());
        assertEquals("my-user", response.name());
        assertEquals("my-email", response.email());
        assertEquals(User.Role.USER, response.role());

        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(contextAuth);
        assertEquals(mockAuth, contextAuth);
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
    public void getAllUsersTest() {
        Pageable pageable = PageRequest.of(0, 10);

        UserProjection mockProjection = mock(UserProjection.class);

        List<UserProjection> contentList = Collections.singletonList(mockProjection);
        Page<UserProjection> mockProjectionPage = new PageImpl<>(contentList, pageable, 1);

        when(userRepository.findAllUserProjections(pageable)).thenReturn(mockProjectionPage);
        when(userMapper.convert(mockProjection)).thenReturn(userDto);

        Page<UserResponseDTO> response = userService.getAllUsers(pageable);

        assertNotNull(response);
        assertEquals(userDto, response.getContent().get(0));
        assertEquals(1, response.getTotalElements());

        verify(userRepository, times(1)).findAllUserProjections(pageable);
        verify(userMapper, times(1)).convert(mockProjection);
    }


    @Test
    public void getUserTest() {
        UserProjection userProjection = mock(UserProjection.class);
        when(userRepository.findUserProjectionById(USER_ID)).thenReturn(Optional.of(userProjection));
        when(userMapper.convert(userProjection)).thenReturn(userDto);

        UserResponseDTO response = userService.getUser(USER_ID);

        assertNotNull(response);
        assertEquals(userDto, response);

        verify(userRepository, times(1)).findUserProjectionById(USER_ID);
        verify(userMapper, times(1)).convert(userProjection);
    }

    @Test
    public void deleteUser_Success() {
        UserProjection mockProjection = mock(UserProjection.class);
        when(userRepository.findUserProjectionById(USER_ID)).thenReturn(Optional.of(mockProjection));
        userService.deleteUser(USER_ID);
        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    public void deleteUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findUserProjectionById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(USER_ID));

        verify(userRepository, times(1)).findUserProjectionById(USER_ID);
        verify(userRepository, never()).deleteById(USER_ID);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

}