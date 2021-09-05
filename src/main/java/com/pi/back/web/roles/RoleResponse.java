package com.pi.back.web.roles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.db.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

    private Integer id;
    private String role;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    public static final RoleResponse newInstance(Role role) {
        return RoleResponse.builder()
                .id(role.getRoleId())
                .role(role.getRoleName())
                .build();
    }

    public static final RoleResponse newDetailedInstance(Role role) {
        return RoleResponse.builder()
                .id(role.getRoleId())
                .role(role.getRoleName())
                .description(role.getDescription())
                .build();
    }
}
