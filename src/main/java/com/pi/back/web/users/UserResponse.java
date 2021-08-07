package com.pi.back.web.users;

import com.pi.back.db.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;
    private String username;
    private String role;

    public static final UserResponse newInstance(Users users) {
        return UserResponse.builder()
                .id(users.getId())
                .username(users.getUsername())
                .role(users.getRole().getRoleName())
                .build();
    }
}
