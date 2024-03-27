package com.example.HW7.mappers;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.repo.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserMapper extends BaseMapper<User, UserDto> { }
