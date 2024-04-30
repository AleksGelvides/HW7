package com.example.HW7.controllers;

import com.example.HW7.data.dto.UserDto;
import com.example.HW7.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Flux<UserDto> getAll() {
        return service.findAll();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserDto>> findById(@RequestParam String userId) {
        return service.findById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserDto>> createUser(@RequestBody UserDto userDto) {
        return service.saveOrUpdate(userDto, null)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<UserDto>> changeUser(@RequestBody UserDto userDto,
                                                    @RequestParam String userId) {
        return service.saveOrUpdate(userDto, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER')")
    public Mono<ResponseEntity<Void>> delete(@RequestParam String userId) {
        return service.deleteById(userId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
