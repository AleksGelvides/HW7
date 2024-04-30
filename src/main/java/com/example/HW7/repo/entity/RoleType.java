package com.example.HW7.repo.entity;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType {
    ROLE_USER, ROLE_MANAGER;

    public RoleType getRoleType(GrantedAuthority grantedAuthority) {
        return RoleType.valueOf(grantedAuthority.getAuthority());
    }
}
