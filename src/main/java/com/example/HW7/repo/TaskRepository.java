package com.example.HW7.repo;

import com.example.HW7.repo.entity.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> { }
