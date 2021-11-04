package com.pi.back.web;

import com.pi.back.config.security.Privileges;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RolesControllerTest {

    private static final String ROLES_URI_PATH = "/roles";

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
            List<Privileges> roleList = List.of(Privileges.ROLE_ADMIN);

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get(ROLES_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles", hasSize(1)))
                    .andExpect(jsonPath("$.roles.[0].id", is(roleList.get(0).getRoleId())))
                    .andExpect(jsonPath("$.roles.[0].role", is(roleList.get(0).getRole())));
        }

        @Test
        @DisplayName("when no roles are found then return 204 no content")
        void rolesNotFound() throws Exception {
            List<Privileges> roleList = List.of();

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
        void roleOneFound() throws Exception {
            List<Privileges> roleList = List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT);

            when(rolesService.findAll()).thenReturn(roleList);

            for (int id = 0; id < roleList.size(); id++) {
                mockMvc.perform(get("/roles/" + (id + 1))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(roleList.get(id).getRoleId())))
                        .andExpect(jsonPath("$.role", is(roleList.get(id).getRole())))
                        .andExpect(jsonPath("$.description", is(roleList.get(id).getDescription())));
            }
        }

        @Test
        @DisplayName("when specific role is not found then return 404 not found")
        void roleNotFound() throws Exception {
            List<Privileges> roleList = List.of();

            when(rolesService.findAll()).thenReturn(roleList);

            mockMvc.perform(get("/roles/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}