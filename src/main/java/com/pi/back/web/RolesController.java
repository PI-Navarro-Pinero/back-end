package com.pi.back.web;

import com.pi.back.db.Role;
import com.pi.back.services.RolesService;
import com.pi.back.web.roles.RoleResponse;
import com.pi.back.web.roles.RolesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_R;

@RestController
public class RolesController {

    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Secured(ROLE_R)
    @GetMapping("/roles")
    public ResponseEntity<RolesResponse> fetchRoles() {
        List<Role> rolesList = rolesService.findAll();

        final List<RoleResponse> rolesListResponse = rolesList.stream()
                .distinct()
                .map(RoleResponse::newInstance)
                .collect(Collectors.toList());

        if (rolesListResponse.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(RolesResponse
                .builder()
                .roles(rolesListResponse)
                .build());
    }

    @Secured(ROLE_R)
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> fetchRole(@PathVariable(name = "roleId") Integer roleId) {
        List<Role> rolesList = rolesService.findAll();

        return rolesList.stream()
                .distinct()
                .filter(r -> r.getRoleId().equals(roleId))
                .findFirst()
                .map(RoleResponse::newDetailedInstance)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
