package com.pi.back.db;

import com.pi.back.config.security.Privileges;
import com.pi.back.users.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {

    @Nested
    class GetRolesTestCase {
        @Test
        @DisplayName("when user has any role then return empty list")
        void userHasNonePrivileges() {
            User sut = User.builder().build();

            List<Privileges> actual = sut.getRoles();

            assertThat(actual).isEqualTo(List.of());
        }

        @Test
        @DisplayName("when user has role ADMIN then return list containing ROLE_ADMIN")
        void userHasRoleAdmin() {
            User sut = User.builder().roleAdmin(true).build();

            List<Privileges> actual = sut.getRoles();

            assertThat(actual).isEqualTo(List.of(Privileges.ROLE_ADMIN));
        }

        @Test
        @DisplayName("when user has role AGENT then return list containing ROLE_AGENT")
        void userHasRoleAgent() {
            User sut = User.builder().roleAgent(true).build();

            List<Privileges> actual = sut.getRoles();

            assertThat(actual).isEqualTo(List.of(Privileges.ROLE_AGENT));
        }

        @Test
        @DisplayName("when user has role AGENT and ADMIN then return list containing ROLE_AGENT and ROLE_ADMIN")
        void userHasBothRoleAgentAndAdmin() {
            User sut = User.builder().roleAgent(true).roleAdmin(true).build();

            List<Privileges> actual = sut.getRoles();

            assertThat(actual).isEqualTo(List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT));
        }
    }
}