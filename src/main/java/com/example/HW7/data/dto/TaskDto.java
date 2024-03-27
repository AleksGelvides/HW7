package com.example.HW7.data.dto;

import com.example.HW7.data.emums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private String id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private TaskStatus status;
    private String authorId;
    private String assigneeId;
    private Set<String> observerIds;
    private UserDto author;
    private UserDto assignee;
    private Set<UserDto> observers;
}
