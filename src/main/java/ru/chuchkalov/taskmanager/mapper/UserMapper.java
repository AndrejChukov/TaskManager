package ru.chuchkalov.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chuchkalov.taskmanager.dto.projection.UserProjection;
import ru.chuchkalov.taskmanager.dto.request.UserRegistrationRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.UserResponseDTO;
import ru.chuchkalov.taskmanager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO convert(User user);

    UserResponseDTO convert(UserProjection projection);

    User toEntity(UserResponseDTO dto);
    User registerToEntity(UserRegistrationRequestDTO userRegistrationRequest);
}
