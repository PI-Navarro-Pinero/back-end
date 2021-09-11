package com.pi.back.web.roles;

import com.pi.back.db.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleResponseTest {

    @Test
    @DisplayName("create RoleResponse from Role")
    void newInstanceTest() {
        Role role = Role.builder()
                .id(1)
                .roleId(2)
                .roleName("foo")
                .userId(3)
                .description("foobar")
                .build();

        RoleResponse actual = RoleResponse.newInstance(role);

        assertThat(actual.getId()).isEqualTo(role.getRoleId());
        assertThat(actual.getRole()).isEqualTo(role.getRoleName());
    }

    @Test
    @DisplayName("create detailed RoleResponse from Role")
    void newDetailedInstanceTest() {
        Role role = Role.builder()
                .id(1)
                .roleId(2)
                .roleName("foo")
                .userId(3)
                .description("foobar")
                .build();

        RoleResponse actual = RoleResponse.newDetailedInstance(role);

        assertThat(actual.getId()).isEqualTo(role.getRoleId());
        assertThat(actual.getRole()).isEqualTo(role.getRoleName());
        assertThat(actual.getDescription()).isEqualTo(role.getDescription());
    }
}