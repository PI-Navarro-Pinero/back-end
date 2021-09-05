package com.pi.back.web;

import com.pi.back.db.User;
import com.pi.back.services.UsersService;
import com.pi.back.web.users.UserRequest;
import com.pi.back.web.users.UserResponse;
import com.pi.back.web.users.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.InvalidAttributesException;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_R;
import static com.pi.back.config.security.Privileges.Roles.ROLE_W;

@RestController
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Secured(ROLE_R)
    @GetMapping("/users")
    public ResponseEntity<UsersResponse> fetchUsers() {
        List<User> usersList = usersService.findAll();

        final List<UserResponse> usersListResponse = usersList.stream()
                .map(UserResponse::newInstance)
                .collect(Collectors.toList());
        if (usersListResponse.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UsersResponse
                .builder()
                .users(usersListResponse)
                .build());
    }

    @Secured(ROLE_R)
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> fetchUser(@PathVariable(name = "userId") Integer userId) {
        try {
            User user = usersService.findUser(userId);
            final UserResponse userResponse = UserResponse.newDetailedInstance(user);
            return ResponseEntity.ok(userResponse);
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_W)
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        try {
            User createdUser = usersService.create(request);
            return ResponseEntity.ok(UserResponse.newDetailedInstance(createdUser));
        } catch (InvalidAttributesException invalidAttributesException) {
            return ResponseEntity.badRequest().body(UserResponse.newErrorInstance(invalidAttributesException));
        }
    }

    @Secured(ROLE_W)
    @PutMapping("/users")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest request) {
        try {
            User updatedUser = usersService.update(request);
            return ResponseEntity.ok(UserResponse.newDetailedInstance(updatedUser));
        } catch (InvalidParameterException | ClassNotFoundException | InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(UserResponse.newErrorInstance(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_W)
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable(name = "userId") Integer userId) {
        try {
            usersService.delete(userId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.notFound().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(UserResponse.newErrorInstance(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
