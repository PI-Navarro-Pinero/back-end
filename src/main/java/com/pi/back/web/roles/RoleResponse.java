package com.pi.back.web.roles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.config.security.Privileges;
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

    public static final RoleResponse newInstance(Privileges privilege) {
        return RoleResponse.builder()
                .id(privilege.getRoleId())
                .role(privilege.getRole())
                .build();
    }

    public static final RoleResponse newDetailedInstance(Privileges privilege) {
        return RoleResponse.builder()
                .id(privilege.getRoleId())
                .role(privilege.getRole())
                .description(privilege.getDescription())
                .build();
    }
}
