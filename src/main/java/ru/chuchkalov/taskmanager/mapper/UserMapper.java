package ru.chuchkalov.taskmanager.mapper;

import org.mapstruct.Mapper;
import ru.chuchkalov.taskmanager.dto.request.UserRegistrationRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO convert(User user);
    User toEntity(UserResponseDTO dto);
    User registerToEntity(UserRegistrationRequestDTO userRegistrationRequest);
}
