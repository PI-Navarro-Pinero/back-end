package com.pi.back.users.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.config.security.Privileges;
import com.pi.back.users.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Integer id;
    private String username;
    private String fullname;
    private String license;

    @Schema(example = "[ ROLE_ADMIN ]")
    private List<Privileges> privileges;

    public static final UserResponse newInstance(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .privileges(user.getRoles())
                .build();
    }

    public static final UserResponse newDetailedInstance(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .license(user.getLicense())
                .privileges(user.getRoles())
                .build();
    }
}
