package com.pi.back.web;

import com.pi.back.db.Role;
import com.pi.back.services.RolesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RolesControllerTest {

    private static final String ROLES_URI_PATH = "/roles";
    private static final String ROLE_URI_PATH = "/roles/1";

    private MockMvc mockMvc;

    @Mock
    private RolesService rolesService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RolesController(rolesService))
                .build();
    }

    @Nested
    @DisplayName("GET /roles")
    class fetchRolesTest {

        @Test
        @DisplayName("when roles are found then return 200 ok")
        void rolesFound() throws Exception {
            Role mockRole = createMockRole();

            List<Role> roleList = List.of(mockRole);

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get(ROLES_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles.[0].id", is(mockRole.getRoleId())))
                    .andExpect(jsonPath("$.roles.[0].role", is(mockRole.getRoleName())));
        }

        @Test
        @DisplayName("when no roles are found then return 204 no content")
        void rolesNotFound() throws Exception {
            List<Role> roleList = List.of();

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get(ROLES_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /roles/{roleId}")
    class fetchRoleTest {

        @Test
        @DisplayName("when specific role is found then return 200 ok")
        void roleFound() throws Exception {
            Role mockRole = createMockRole();

            List<Role> roleList = List.of(mockRole);

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get(ROLE_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(mockRole.getRoleId())))
                    .andExpect(jsonPath("$.role", is(mockRole.getRoleName())))
                    .andExpect(jsonPath("$.description", is(mockRole.getDescription())));
        }

        @Test
        @DisplayName("when specific role is not found then return 204 no content")
        void roleNotFound() throws Exception {
            List<Role> roleList = List.of();

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get(ROLE_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    private Role createMockRole() {
        System.out.println("role creado");
        return Role.builder()
                .id(1)
                .roleId(1)
                .roleName("foo")
                .userId(1)
                .description("bar")
                .build();
    }
}