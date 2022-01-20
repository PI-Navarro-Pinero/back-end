package com.pi.back.web.roles;

import com.pi.back.config.security.Privileges;
import com.pi.back.roles.model.RoleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleResponseTest {

    @Test
    @DisplayName("create RoleResponse from Role")
    void newInstanceTest() {
        RoleResponse actual = RoleResponse.newInstance(Privileges.ROLE_ADMIN);

        assertThat(actual.getId()).isEqualTo(Privileges.ROLE_ADMIN.getRoleId());
        assertThat(actual.getRole()).isEqualTo(Privileges.ROLE_ADMIN.getRole());
        assertThat(actual.getDescription()).isEqualTo(Privileges.ROLE_ADMIN.getDescription());
    }
}