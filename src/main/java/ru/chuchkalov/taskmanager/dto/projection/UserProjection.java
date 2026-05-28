package ru.chuchkalov.taskmanager.dto.projection;

import ru.chuchkalov.taskmanager.entity.User;

public interface UserProjection {
    String getUsername();
    String getEmail();
    User.Role getRole();
}
