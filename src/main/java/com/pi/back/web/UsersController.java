package com.pi.back.web;

import com.pi.back.db.Users;
import com.pi.back.services.UsersService;
import com.pi.back.web.users.UserResponse;
import com.pi.back.web.users.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/users")
    public ResponseEntity<UsersResponse> fetchUsers() {
        List<Users> usersList = usersService.findAll();

        final List<UserResponse> usersListResponse = usersList.stream()
                .map(UserResponse::newInstance)
                .collect(Collectors.toList());
        if(usersListResponse.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(UsersResponse
            .builder()
            .users(usersListResponse)
            .build());
    }
}
