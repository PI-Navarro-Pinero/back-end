package com.pi.back.services;

import com.pi.back.config.security.Privileges;
import com.pi.back.db.User;
import com.pi.back.db.UsersRepository;
import com.pi.back.web.users.UserDTO;
import com.pi.back.web.users.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.naming.directory.InvalidAttributesException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService sut;

    @Nested
    @DisplayName("find all users in repository")
    class findAllTest {
        @Test
        void findAll() {
            User mockUser = User.builder()
                    .id(1)
                    .username("foo")
                    .fullname("bar")
                    .password("baz")
                    .roles(List.of(Privileges.ROLE_ADMIN))
                    .license("foobar")
                    .build();

            when(usersRepository.findAll()).thenReturn(List.of(mockUser));

            List<User> actual = sut.findAll();

            assertThat(actual).isEqualTo(List.of(mockUser));
        }
    }

    @Nested
    @DisplayName("find a user in repository")
    class findUserTest {

        @Test
        @DisplayName("when user is found then return User")
        void userFound() {
            User mockUser = User.builder()
                    .id(1)
                    .username("foo")
                    .fullname("bar")
                    .password("baz")
                    .roles(List.of(Privileges.ROLE_ADMIN))
                    .license("foobar")
                    .build();

            when(usersRepository.findById(any())).thenReturn(Optional.of(mockUser));

            User actual = sut.findUser(1);

            assertThat(actual).isEqualTo(mockUser);
        }

        @Test
        @DisplayName("when user is not found then throw NoSuchElementException")
        void userNotFound() {
            when(usersRepository.findById(any())).thenThrow(new NoSuchElementException());

            assertThrows(NoSuchElementException.class, () -> sut.findUser(any()));
        }
    }

    @Nested
    @DisplayName("create a new user")
    class createTest {

        @Test
        @DisplayName("when condition is not met then create user")
        void createUser() throws InvalidAttributesException {
            final UserRequest mockUserRequest = UserRequest.builder()
                    .username("foo")
                    .fullname("bar")
                    .license("foobar")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            User mockUser = User.builder()
                    .username(mockUserRequest.getUsername())
                    .fullname(mockUserRequest.getFullname())
                    .password("foobar")
                    .roles(mockUserRequest.getPrivileges())
                    .license(mockUserRequest.getLicense())
                    .build();

            List<User> users = createUsers();

            when(usersRepository.findAll()).thenReturn(users);
            when(passwordEncoder.encode(any())).thenReturn("foobar");

            sut.create(mockUserRequest);

            verify(usersRepository).save(mockUser);
        }

        @Test
        @DisplayName("when username condition is met then throw InvalidAttributesException")
        void createUserWithExistingUsername() {
            final UserRequest mockUserRequest = UserRequest.builder()
                    .username("foo 0")
                    .fullname("bar")
                    .license("foobar")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            List<User> users = createUsers();

            when(usersRepository.findAll()).thenReturn(users);
            when(passwordEncoder.encode(any())).thenReturn("foobar");

            assertThrows(InvalidAttributesException.class, () -> sut.create(mockUserRequest));
            verify(usersRepository, never()).save(any());
        }

        @Test
        @DisplayName("when license condition is met then throw InvalidAttributesException")
        void createUserWithExistingLicense() {
            final UserRequest mockUserRequest = UserRequest.builder()
                    .username("foo")
                    .fullname("bar")
                    .license("foobar 0")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            List<User> users = createUsers();

            when(usersRepository.findAll()).thenReturn(users);
            when(passwordEncoder.encode(any())).thenReturn("foobar");

            assertThrows(InvalidAttributesException.class, () -> sut.create(mockUserRequest));
            verify(usersRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update existing user")
    class updateTest {

        @Test
        @DisplayName("when user to update has id 0 then throw InvalidParameterException")
        void updateUserWithIdZero() {
            final UserDTO dto = UserDTO.builder()
                    .id(0)
                    .username("foo")
                    .fullname("bar")
                    .license("foobar")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            assertThrows(InvalidParameterException.class, () -> sut.update(dto));
        }

        @Test
        @DisplayName("when user to update has id null then throw InvalidParameterException")
        void updateUserWithIdNull() {
            final UserDTO dto = UserDTO.builder()
                    .id(null)
                    .username("foo")
                    .fullname("bar")
                    .license("foobar")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            assertThrows(InvalidParameterException.class, () -> sut.update(dto));
        }

        @Test
        @DisplayName("when username condition is met then throw InvalidAttributesException")
        void updateUserUsingExistingUsername() {
            final UserDTO dto = UserDTO.builder()
                    .id(1)
                    .username("foo 2")
                    .fullname("bar 1")
                    .license("foobar 1")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            when(usersRepository.findAll()).thenReturn(createUsers());

            assertThrows(InvalidAttributesException.class, () -> sut.update(dto));
        }

        @Test
        @DisplayName("when license condition is met then throw InvalidAttributesException")
        void updateUserUsingExistingLicense() {
            final UserDTO dto = UserDTO.builder()
                    .id(1)
                    .username("foo 1")
                    .fullname("bar 1")
                    .license("foobar 2")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            when(usersRepository.findAll()).thenReturn(createUsers());

            assertThrows(InvalidAttributesException.class, () -> sut.update(dto));
        }

        @Test
        @DisplayName("when user to update is non existent then throw ClassNotFoundException")
        void updateNonExistentUser() {
            final UserDTO dto = UserDTO.builder()
                    .id(10)
                    .username("foo 10")
                    .fullname("bar 10")
                    .license("foobar 10")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            when(usersRepository.findAll()).thenReturn(createUsers());

            assertThrows(ClassNotFoundException.class, () -> sut.update(dto));
        }

        @Test
        @DisplayName("when username conditions are met and user exists then update user")
        void updateUserUsername() throws Exception {
            final UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("new foo 1")
                    .fullname("bar 1")
                    .license("foobar 1")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            User mockUserToUpdate = User.builder()
                    .id(userDTO.getId())
                    .username(userDTO.getUsername())
                    .password("_" + userDTO.getUsername())
                    .fullname(userDTO.getFullname())
                    .roles(userDTO.getPrivileges())
                    .license(userDTO.getLicense())
                    .build();

            when(passwordEncoder.encode(any())).thenReturn("_" + userDTO.getUsername());
            when(usersRepository.findAll()).thenReturn(createUsers());

            sut.update(userDTO);

            verify(usersRepository).save(mockUserToUpdate);
        }

        @Test
        @DisplayName("when license conditions are met and user exists then update user")
        void updateUserLicense() throws Exception {
            final UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("foo 1")
                    .fullname("bar 1")
                    .license("new foobar 1")
                    .privileges(List.of(Privileges.ROLE_ADMIN))
                    .build();

            User mockUserToUpdate = User.builder()
                    .id(userDTO.getId())
                    .username(userDTO.getUsername())
                    .password("_" + userDTO.getUsername())
                    .fullname(userDTO.getFullname())
                    .roles(userDTO.getPrivileges())
                    .license(userDTO.getLicense())
                    .build();

            when(passwordEncoder.encode(any())).thenReturn("_" + userDTO.getUsername());
            when(usersRepository.findAll()).thenReturn(createUsers());

            sut.update(userDTO);

            verify(usersRepository).save(mockUserToUpdate);
        }
    }

    @Nested
    @DisplayName("delete existing user")
    class deleteTest {

        @Test
        @DisplayName("when user to delete has id 0 then throw InvalidParameterException")
        void deleteUserWithIdZero() {
            User mockUserToDelete = User.builder()
                    .build();

            when(usersRepository.findById(any())).thenReturn(Optional.of(mockUserToDelete));

            assertThrows(InvalidParameterException.class, () -> sut.delete(0));
        }

        @Test
        @DisplayName("when user to delete has non existent then throw NoSuchElementException")
        void deleteUserWithIdNull() {
            when(usersRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> sut.delete(any()));
        }

        @Test
        @DisplayName("when user to delete is valid then delete")
        void deleteUser() {
            User mockUserToDelete = User.builder()
                    .build();

            when(usersRepository.findById(any())).thenReturn(Optional.of(mockUserToDelete));

            sut.delete(1);

            verify(usersRepository).delete(mockUserToDelete);
        }
    }

    private List<User> createUsers() {
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            userList.add(User.builder()
                    .id(i)
                    .username("foo " + i)
                    .fullname("bar " + i)
                    .password("baz " + i)
                    .roles(List.of(Privileges.ROLE_ADMIN))
                    .license("foobar " + i)
                    .build());
        }

        return userList;
    }
}