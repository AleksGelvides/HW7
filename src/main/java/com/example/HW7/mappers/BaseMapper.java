package com.example.HW7.mappers;

import org.mapstruct.*;

@MapperConfig(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BaseMapper<ENTITY, DTO> {

    ENTITY toEntity(DTO dto);

    DTO toDto(ENTITY entity);

    ENTITY merge(@MappingTarget ENTITY oldEntity, ENTITY newEntity);
}
