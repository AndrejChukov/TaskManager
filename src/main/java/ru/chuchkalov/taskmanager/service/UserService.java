package ru.chuchkalov.taskmanager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chuchkalov.taskmanager.dto.projection.UserProjection;
import ru.chuchkalov.taskmanager.dto.request.UserRegistrationRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.LoginResponseDTO;
import ru.chuchkalov.taskmanager.dto.request.LoginRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.exception.EntityNotFoundException;
import ru.chuchkalov.taskmanager.mapper.UserMapper;
import ru.chuchkalov.taskmanager.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public UserResponseDTO register(UserRegistrationRequestDTO request) {
        User user = userMapper.registerToEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);

        user = userRepository.save(user);

        return userMapper.convert(user);
    }

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(), loginRequestDTO.password()
                ));

        SecurityContextHolder.getContext().setAuthentication(auth);

        var user = (User) auth.getPrincipal();
        String token = tokenService.generateToken(auth);
        return new LoginResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Transactional
    public UserResponseDTO createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User {} created successfully", user.getId());
        return userMapper.convert(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsers(Pageable pageable) {
        return userRepository.findAllUserProjections(pageable).map(userMapper::convert);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUser(Long id) {
        return userMapper.convert(getUserProjection(id));
    }

    @Transactional
    public void deleteUser(Long id) {
        getUserProjection(id);
        userRepository.deleteById(id);
        log.info("User {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public UserProjection getUserProjection(Long id) {
        return userRepository.findUserProjectionById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID: " + id + " not found"));
    }
}
