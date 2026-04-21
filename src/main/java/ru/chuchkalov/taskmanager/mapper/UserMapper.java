package ru.chuchkalov.taskmanager.mapper;

import org.mapstruct.Mapper;
import ru.chuchkalov.taskmanager.dto.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO convert(User user);
    User toEntity(UserResponseDTO dto);
}
