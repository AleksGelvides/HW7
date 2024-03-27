package com.example.HW7.mappers;

import com.example.HW7.data.dto.TaskDto;
import com.example.HW7.repo.entity.Task;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class, uses = UserMapper.class)
public interface TaskMapper extends BaseMapper<Task, TaskDto> {
    @Override
    TaskDto toDto(Task task);
}
