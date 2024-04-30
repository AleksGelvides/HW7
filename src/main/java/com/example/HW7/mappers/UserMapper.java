package com.example.HW7.mappers;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.repo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface UserMapper extends BaseMapper<User, UserDto> {
    @Override
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDto userDto);

    @Override
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User merge(@MappingTarget User oldEntity, User newEntity);
}
