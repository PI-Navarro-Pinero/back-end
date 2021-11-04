package com.pi.back.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.back.config.security.Privileges;
import com.pi.back.db.User;
import com.pi.back.services.UsersService;
import com.pi.back.web.users.UserRequest;
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

import javax.naming.directory.InvalidAttributesException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    private static final String USERS_URI_PATH = "/users";
    private static final String USER_URI_PATH = "/users/1";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private UsersService usersService;

    private final User mockUser = createMockUser();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsersController(usersService))
                .build();
    }

    @Nested
    @DisplayName("GET /users")
    class fetchUsersTest {

        @Test
        @DisplayName("when one user is found then return 200 ok")
        void userFound() throws Exception {
            when(usersService.findAll()).thenReturn(List.of(mockUser));

            mockMvc.perform(get(USERS_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.users", hasSize(1)))
                    .andExpect(jsonPath("$.users.[0].id", is(mockUser.getId())))
                    .andExpect(jsonPath("$.users.[0].username", is(mockUser.getUsername())))
                    .andExpect(jsonPath("$.users.[0].privileges.[0]", is(mockUser.getRoles().get(0).getRole())));
        }

        @Test
        @DisplayName("when no users are found then return 204 no content")
        void userNotFound() throws Exception {
            when(usersService.findAll()).thenReturn(List.of());

            mockMvc.perform(get(USERS_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /users/{userId}")
    class fetchUserTest {

        @Test
        @DisplayName("when user is found then return 200 ok")
        void userFound() throws Exception {
            when(usersService.findUser(any())).thenReturn(mockUser);

            mockMvc.perform(get(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(mockUser.getId())))
                    .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                    .andExpect(jsonPath("$.fullname", is(mockUser.getFullname())))
                    .andExpect(jsonPath("$.license", is(mockUser.getLicense())))
                    .andExpect(jsonPath("$.privileges.[0]", is(mockUser.getRoles().get(0).getRole())));
        }

        @Test
        @DisplayName("when NoSuchElementException then return 404 not found")
        void noSuchElementExceptionThrown() throws Exception {
            when(usersService.findUser(any())).thenThrow(new NoSuchElementException());

            mockMvc.perform(get(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("when Exception then return 500 internal server error")
        void exceptionThrown() throws Exception {
            when(usersService.findUser(any())).thenThrow(new RuntimeException());

            mockMvc.perform(get(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /users")
    class createUserTest {

        UserRequest mockRequest = createMockRequest();

        @Test
        @DisplayName("when user is created then return 200 ok")
        void createdUser() throws Exception {
            when(usersService.create(any())).thenReturn(mockUser);

            mockMvc.perform(post(USERS_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(mockUser.getId())))
                    .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                    .andExpect(jsonPath("$.fullname", is(mockUser.getFullname())))
                    .andExpect(jsonPath("$.license", is(mockUser.getLicense())))
                    .andExpect(jsonPath("$.privileges.[0]", is(mockUser.getRoles().get(0).getRole())));
        }

        @Test
        @DisplayName("when InvalidAttributesException then return 412 precondition failed")
        void invalidAttributesExceptionThrown() throws Exception {
            when(usersService.create(any())).thenThrow(new InvalidAttributesException("foo explanation"));

            mockMvc.perform(post(USERS_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is("foo explanation")));
        }

        @Test
        @DisplayName("when Exception then return 500 internal server error")
        void exceptionThrown() throws Exception {
            when(usersService.create(any())).thenThrow(new RuntimeException());

            mockMvc.perform(post(USERS_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("PUT /users/{userId}")
    class updateUserTest {

        UserRequest mockRequest = createMockRequest();

        @Test
        @DisplayName("when user is updated then return 200 ok")
        void updatedUser() throws Exception {
            when(usersService.update(any())).thenReturn(mockUser);

            mockMvc.perform(put(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("when InvalidParameterException then return 412 precondition failed")
        void invalidParameterExceptionThrown() throws Exception {
            String exceptionMessage = "foo message";

            when(usersService.update(any())).thenThrow(new InvalidParameterException(exceptionMessage));

            mockMvc.perform(put(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(exceptionMessage)));
        }

        @Test
        @DisplayName("when InvalidAttributesException then return 412 precondition failed")
        void invalidAttributesExceptionThrown() throws Exception {
            String exceptionMessage = "foo message";

            when(usersService.update(any())).thenThrow(new InvalidAttributesException(exceptionMessage));

            mockMvc.perform(put(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(exceptionMessage)));
        }

        @Test
        @DisplayName("when ClassNotFoundException then return 204 no content")
        void classNotFoundExceptionThrown() throws Exception {
            String exceptionMessage = "foo message";

            when(usersService.update(any())).thenThrow(new ClassNotFoundException(exceptionMessage));

            mockMvc.perform(put(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$.error", is(exceptionMessage)));
        }

        @Test
        @DisplayName("when Exception then return 500 internal server error")
        void exceptionThrown() throws Exception {
            when(usersService.update(any())).thenThrow(new RuntimeException());

            mockMvc.perform(put(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockRequest)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("DELETE /users/{userId}")
    class deleteUserTest {

        @Test
        @DisplayName("when deleted user then return 200 ok")
        void deletedUser() throws Exception {
            mockMvc.perform(delete(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("when NoSuchElementException then return 404 not found")
        void noSuchElementExceptionThrown() throws Exception {
            doThrow(new NoSuchElementException()).when(usersService).delete(anyInt());

            mockMvc.perform(delete(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("when InvalidParameterException then return 412 precondition failed")
        void invalidParameterExceptionThrown() throws Exception {
            String exceptionMessage = "foo message";

            doThrow(new InvalidParameterException(exceptionMessage)).when(usersService).delete(anyInt());

            mockMvc.perform(delete(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(exceptionMessage)));
        }

        @Test
        @DisplayName("when Exception then return 500 internal server error")
        void exceptionThrown() throws Exception {
            doThrow(new RuntimeException()).when(usersService).delete(anyInt());

            mockMvc.perform(delete(USER_URI_PATH)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    private User createMockUser() {
        return User.builder()
                .id(1)
                .username("foo")
                .password("bar")
                .fullname("baz")
                .license("qux")
                .roles(List.of(Privileges.ROLE_ADMIN))
                .build();
    }

    private UserRequest createMockRequest() {
        return UserRequest.builder()
                .username(mockUser.getUsername())
                .fullname(mockUser.getFullname())
                .license(mockUser.getLicense())
                .privileges(mockUser.getRoles())
                .build();
    }

    private static String asJsonString(Object request) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(request);
    }
}