package ru.chuchkalov.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chuchkalov.taskmanager.dto.TaskResponseDTO;
import ru.chuchkalov.taskmanager.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(source = "user.username", target = "ownerName")
    TaskResponseDTO convert(Task task);

    Task toEntity(TaskResponseDTO dto);
}
