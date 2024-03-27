package com.example.HW7.controllers;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public Flux<UserDto> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDto>> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserDto>> createUser(@RequestBody UserDto userDto) {
        return service.saveOrUpdate(userDto, null)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserDto>> changeUser(@RequestBody UserDto userDto,
                                                    @PathVariable String id) {
        return service.saveOrUpdate(userDto, id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
