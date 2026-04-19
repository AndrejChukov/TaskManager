package ru.chuchkalov.taskmanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {
    @GetMapping("/status")
    public String getStatus() {
        return "System is online";
    }
}
