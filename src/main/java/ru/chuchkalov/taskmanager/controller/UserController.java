package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.request.UserRegistrationRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.LoginResponseDTO;
import ru.chuchkalov.taskmanager.dto.request.LoginRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.service.UserService;

@RestController
@RequestMapping(path = "/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public UserResponseDTO register(@RequestBody @Valid UserRegistrationRequestDTO request) {
        return userService.register(request);
    }

    @PostMapping("/auth/signin")
    public LoginResponseDTO authenticate(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return userService.authenticate(loginRequestDTO);
    }

    @PostMapping(path = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @GetMapping(path = "/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDTO> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @GetMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @DeleteMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
