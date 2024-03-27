package com.example.HW7.repo;

import com.example.HW7.repo.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> { }
