package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.service.UserService;

@RestController
@RequestMapping(path = "/api")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/users")
    @ResponseStatus(HttpStatus.CREATED)
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
}
