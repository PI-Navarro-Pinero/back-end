package com.pi.back.web.users;

import com.pi.back.config.security.Privileges;
import com.pi.back.users.User;
import com.pi.back.users.model.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    @DisplayName("create UserResponse from User")
    void newInstanceTest() {
        User user = User.builder()
                .id(1)
                .fullname("foo")
                .license("bar")
                .roles(List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT))
                .password("baz")
                .username("qux")
                .build();

        UserResponse actual = UserResponse.newInstance(user);

        assertThat(actual.getId()).isEqualTo(user.getId());
        assertThat(actual.getUsername()).isEqualTo(user.getUsername());
        assertThat(actual.getPrivileges()).isEqualTo(user.getRoles());
    }

    @Test
    @DisplayName("create detailed UserResponse from User")
    void newDetailedInstanceTest() {
        User user = User.builder()
                .id(1)
                .fullname("foo")
                .license("bar")
                .roles(List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT))
                .password("baz")
                .username("qux")
                .build();

        UserResponse actual = UserResponse.newDetailedInstance(user);

        assertThat(actual.getId()).isEqualTo(user.getId());
        assertThat(actual.getUsername()).isEqualTo(user.getUsername());
        assertThat(actual.getFullname()).isEqualTo(user.getFullname());
        assertThat(actual.getLicense()).isEqualTo(user.getLicense());
        assertThat(actual.getPrivileges()).isEqualTo(user.getRoles());
    }
}