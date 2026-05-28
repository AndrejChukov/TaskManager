package ru.chuchkalov.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chuchkalov.taskmanager.dto.projection.TaskProjection;
import ru.chuchkalov.taskmanager.dto.request.TaskRequestDTO;
import ru.chuchkalov.taskmanager.dto.response.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(source = "user.username", target = "ownerName")
    TaskResponseDTO convert(Task task);

    @Mapping(source = "userUsername", target = "ownerName")
    TaskResponseDTO convert(TaskProjection projection);

    Task toEntity(TaskRequestDTO dto);
}
