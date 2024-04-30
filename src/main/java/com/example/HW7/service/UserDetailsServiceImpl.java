package com.example.HW7.service;

import com.example.HW7.data.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByUsername(username)
                .flatMap(user -> Mono.just(new AppUserPrincipal(user)));
    }
}
