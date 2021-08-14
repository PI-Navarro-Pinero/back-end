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
    ROLE_ROOT(Roles.ROLE_ROOT),
    ROLE_ADMIN(Roles.ROLE_ADMIN),
    ROLE_USER(Roles.ROLE_USER);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Roles {
        public static final String ROLE_ROOT = "ROLE_ROOT";
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";
    }

    private final String role;
}
