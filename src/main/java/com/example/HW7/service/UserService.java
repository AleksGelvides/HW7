package com.example.HW7.service;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.mappers.UserMapper;
import com.example.HW7.repo.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public Flux<UserDto> findAll() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    public Mono<UserDto> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Mono<UserDto> saveOrUpdate(UserDto newDto, @Nullable String id) {
        if (!StringUtils.hasText(id)) {
            newDto.setId(UUID.randomUUID().toString());
            return repository.save(mapper.toEntity(newDto)).map(mapper::toDto);
        } else {
            return repository.findById(id)
                    .flatMap(old -> {
                        mapper.merge(old, mapper.toEntity(newDto));
                        return repository.save(old);
                    })
                    .map(mapper::toDto);
        }
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
