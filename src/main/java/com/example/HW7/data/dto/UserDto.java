package com.example.HW7.data.dto;

import com.example.HW7.repo.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDto {
    private String id;
    private String username;
    private String password;
    private String emai;
    private Set<RoleType> roles = new HashSet<>();
}
