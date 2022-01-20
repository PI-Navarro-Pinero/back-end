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
    ROLE_ADMIN(1, Roles.ROLE_ADMIN, "Permite gestionar los usuarios de la aplicación y consultar roles disponibles."),
    ROLE_AGENT(2, Roles.ROLE_AGENT, "Concede permisos para realizar acciones sobre un objetivo.");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_AGENT = "ROLE_AGENT";
    }

    private final Integer roleId;
    private final String role;
    private final String description;

    public enum Values {
        ROLE_ADMIN,
        ROLE_AGENT
    }
}
