package com.example.HW7.repo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "users")
public class User {
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed(unique = true)
    private String emai;
    @Field("roles")
    private Set<RoleType> roles = new HashSet<>();
}
