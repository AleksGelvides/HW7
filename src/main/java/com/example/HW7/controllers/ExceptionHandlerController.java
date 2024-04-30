package com.example.HW7.controllers;

import com.example.HW7.exceptions.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(AppException.class)
    public Mono<ResponseEntity<String>> appException(AppException e) {
        return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
