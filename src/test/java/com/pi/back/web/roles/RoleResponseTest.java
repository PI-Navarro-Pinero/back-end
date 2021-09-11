package com.pi.back.web.roles;

import com.pi.back.config.security.Privileges;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RoleResponseTest {

    @Test
    @DisplayName("create RoleResponse from Role")
    void newInstanceTest() {
        RoleResponse actual = RoleResponse.newInstance(Privileges.ROLE_R);

        assertThat(actual.getId()).isEqualTo(Privileges.ROLE_R.getRoleId());
        assertThat(actual.getRole()).isEqualTo(Privileges.ROLE_R.getRole());
    }

    @Test
    @DisplayName("create detailed RoleResponse from Role")
    void newDetailedInstanceTest() {
        RoleResponse actual = RoleResponse.newDetailedInstance(Privileges.ROLE_W);

        assertThat(actual.getId()).isEqualTo(Privileges.ROLE_W.getRoleId());
        assertThat(actual.getRole()).isEqualTo(Privileges.ROLE_W.getRole());
        assertThat(actual.getDescription()).isEqualTo(Privileges.ROLE_W.getDescription());
    }
}