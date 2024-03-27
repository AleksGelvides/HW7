package com.example.HW7.controllers;

import com.example.HW7.data.dto.TaskDto;
import com.example.HW7.publisher.TaskUpdatesPublisher;
import com.example.HW7.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService service;
    private final TaskUpdatesPublisher publisher;

    @GetMapping
    public Flux<TaskDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskDto>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskDto>> createTask(@RequestBody TaskDto dto) {
        return service.saveOrUpdate(dto, null)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskDto>> changeTask(@RequestBody TaskDto dto, @PathVariable String id) {
        return service.saveOrUpdate(dto, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping(value = "/add-observe")
    public Mono<ResponseEntity<TaskDto>> addObserve(@RequestParam("taskId") String taskId,
                                                    @RequestParam("observeId") String observeId) {
        return service.addObserver(taskId, observeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<TaskDto>> getItemUpdates() {
        return publisher.getUpdateSinks().asFlux()
                .map(itemModel -> ServerSentEvent.builder(itemModel).build());
    }
}
