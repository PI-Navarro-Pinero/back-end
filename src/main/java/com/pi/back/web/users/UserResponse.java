package com.pi.back.web.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.db.Privileges;
import com.pi.back.db.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fullname;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cuil;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
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
                .cuil(user.getCuil())
                .email(user.getEmail())
                .privileges(user.getRoles())
                .build();
    }
}
