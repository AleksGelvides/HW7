package com.example.HW7.service;

import com.example.HW7.data.dto.TaskDto;
import com.example.HW7.data.dto.UserDto;
import com.example.HW7.data.emums.TaskStatus;
import com.example.HW7.mappers.TaskMapper;
import com.example.HW7.publisher.TaskUpdatesPublisher;
import com.example.HW7.repo.TaskRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final UserService userService;
    private final TaskUpdatesPublisher publisher;

    public Flux<TaskDto> findAll() {
        return repository.findAll()
                .flatMap(task -> {
                    TaskDto taskDto = mapper.toDto(task);
                    return setUsers(taskDto)
                            .thenReturn(taskDto);
                });
    }

    public Mono<TaskDto> findById(String id) {
        return repository.findById(id).flatMap(task -> {
            TaskDto taskDto = mapper.toDto(task);
            return setUsers(taskDto)
                    .thenReturn(taskDto);
        });
    }

    public Mono<TaskDto> saveOrUpdate(TaskDto newTask, @Nullable String id) {
        if (!StringUtils.hasText(id)) {
            newTask.setCreatedAt(Instant.now());
            newTask.setStatus(TaskStatus.TODO);
            return repository.save(mapper.toEntity(newTask))
                    .flatMap(task -> {
                        TaskDto taskDto = mapper.toDto(task);
                        return setUsers(taskDto)
                                .thenReturn(taskDto);
                    })
                    .doOnSuccess(publisher::publish);
        } else {
            return repository.findById(id)
                    .flatMap(old -> {
                        old.setUpdatedAt(Instant.now());
                        mapper.merge(old, mapper.toEntity(newTask));
                        return repository.save(old);
                    })
                    .flatMap(task -> {
                        TaskDto taskDto = mapper.toDto(task);
                        return setUsers(taskDto)
                                .thenReturn(taskDto);
                    })
                    .doOnSuccess(publisher::publish);
        }
    }

    public Mono<TaskDto> addObserver(String taskId, String userId) {
        return findById(taskId)
                .flatMap(task -> {
                    task.setUpdatedAt(Instant.now());
                    task.getObserverIds().add(userId);
                    return repository.save(mapper.toEntity(task))
                            .flatMap(retTask -> {
                                TaskDto taskDto = mapper.toDto(retTask);
                                return setUsers(taskDto)
                                        .thenReturn(taskDto);
                            });
                })
                .doOnSuccess(publisher::publish);
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    private Mono<Void> setUsers(TaskDto taskDto) {
        Mono<UserDto> authorMono = userService.findById(taskDto.getAuthorId());
        Mono<UserDto> assigneeMono = userService.findById(taskDto.getAssigneeId());
        Flux<UserDto> observersFlux = Flux.fromIterable(taskDto.getObserverIds())
                .flatMap(userService::findById)
                .collectList()
                .flatMapMany(Flux::fromIterable);

        return Mono.zip(authorMono, assigneeMono, observersFlux.collectList())
                .flatMap(tuple -> {
                    taskDto.setAuthor(tuple.getT1());
                    taskDto.setAssignee(tuple.getT2());
                    taskDto.setObservers(new HashSet<>(tuple.getT3()));
                    return Mono.empty();
                });
    }
}
