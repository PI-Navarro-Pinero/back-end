package com.pi.back.web.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.db.People;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private People person;

    public static final UserResponse newInstance(Users users) {
        return UserResponse.builder()
                .id(users.getId())
                .username(users.getUsername())
                .role(users.getRole().getRoleName())
                .build();
    }

    public static final UserResponse newPersonInstance(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().getRoleName())
                .person(user.getPerson())
                .build();
    }
}
