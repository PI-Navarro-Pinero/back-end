package com.pi.back.config.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public enum Privileges {
    ROLE_ADMIN(1, Roles.ROLE_ADMIN, "Descripción del rol de administrador"),
    ROLE_AGENT(2, Roles.ROLE_AGENT, "Descripción del rol de agente");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_AGENT = "ROLE_AGENT";
    }

    private final Integer roleId;
    private final String role;
    private final String description;
}
