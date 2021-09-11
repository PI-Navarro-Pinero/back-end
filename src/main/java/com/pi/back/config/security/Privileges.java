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
    ROLE_R(1, Roles.ROLE_R, "Descripción del rol de lectura"),
    ROLE_W(2, Roles.ROLE_W, "Descripción del rol de escritura"),
    ROLE_X(3, Roles.ROLE_X, "Descripcion del rol de ejecución");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_R = "ROLE_R";
        public static final String ROLE_W = "ROLE_W";
        public static final String ROLE_X = "ROLE_X";
    }

    private final Integer roleId;
    private final String role;
    private final String description;
}
