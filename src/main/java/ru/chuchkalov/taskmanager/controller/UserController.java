package ru.chuchkalov.taskmanager.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.chuchkalov.taskmanager.dto.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;
import ru.chuchkalov.taskmanager.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "/users/{id}")
    public UserResponseDTO getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }
}
