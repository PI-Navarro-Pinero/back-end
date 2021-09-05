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
    ROLE_R(Roles.ROLE_R),
    ROLE_W(Roles.ROLE_W),
    ROLE_X(Roles.ROLE_X);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_R = "ROLE_R";
        public static final String ROLE_W = "ROLE_W";
        public static final String ROLE_X = "ROLE_X";
    }

    private final String role;
}
