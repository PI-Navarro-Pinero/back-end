package com.pi.back.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public enum Privileges {
    ROLE_R(Roles.ROLE_R, "Descripción del rol de lectura"),
    ROLE_W(Roles.ROLE_W, "Descripción del rol de escritura"),
    ROLE_X(Roles.ROLE_X, "Descripcion del rol de ejecución");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_R = "ROLE_R";
        public static final String ROLE_W = "ROLE_W";
        public static final String ROLE_X = "ROLE_X";
    }

    private final String role;
    private final String description;
}
