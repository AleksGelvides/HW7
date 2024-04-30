package com.example.HW7.service;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.exceptions.AppException;
import com.example.HW7.mappers.UserMapper;
import com.example.HW7.repo.UserRepository;
import com.example.HW7.repo.entity.User;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public Flux<UserDto> findAll() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    public Mono<UserDto> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Mono<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @SneakyThrows
    public Mono<UserDto> saveOrUpdate(UserDto newDto, @Nullable String id) {
        if (!StringUtils.hasText(id)) {
            newDto.setId(UUID.randomUUID().toString());
            if (Objects.isNull(newDto.getPassword())) {
                throw new AppException("Пароль не может быть пустым");
            }
            newDto.setPassword(passwordEncoder.encode(newDto.getPassword()));
            User newUser = mapper.toEntity(newDto)
                    .setPassword(newDto.getPassword())
                    .setRoles(newDto.getRoles());
            return repository.save(newUser)
                    .map(mapper::toDto)
                    .onErrorResume(DuplicateKeyException.class, ex ->
                            Mono.error(new AppException(MessageFormat.format(
                                    "Пользователь {0} c email {1} уже зарегистрирован",
                                    newDto.getUsername(), newDto.getEmai()))));
        } else {
            return repository.findById(id)
                    .flatMap(old -> {
                        old = mapper.merge(old, mapper.toEntity(newDto));
                        if (Objects.nonNull(newDto.getPassword())) {
                            old.setPassword(passwordEncoder.encode(newDto.getPassword()));
                        }
                        if (!CollectionUtils.isEmpty(newDto.getRoles())) {
                            old.getRoles().addAll(newDto.getRoles());
                        }
                        return repository.save(old)
                                .onErrorResume(DuplicateKeyException.class, ex ->
                                        Mono.error(new AppException(MessageFormat.format(
                                                "Изменить логин или email невозможно! Пользователь {0} c email {1} уже зарегистрирован",
                                                newDto.getUsername(), newDto.getEmai()))));
                    })
                    .map(mapper::toDto);
        }
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
