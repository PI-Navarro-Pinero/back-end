package com.pi.back.roles;

import com.pi.back.config.security.Privileges;
import com.pi.back.config.swagger.HttpStatusCodes;
import com.pi.back.config.swagger.SwaggerConfig;
import com.pi.back.config.swagger.SwaggerTags;
import com.pi.back.roles.model.RoleResponse;
import com.pi.back.roles.model.RolesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_ADMIN;

@Slf4j
@RestController
public class RolesController {

    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Operation(
            summary = "Listado de roles disponibles",
            description = "Obtener el listado completo de roles disponibles",
            tags = SwaggerTags.ROLES,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado completo de roles disponibles",
                            content = @Content(
                                    schema = @Schema(implementation = RolesResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NO_CONTENT,
                            description = "No se encontró ningún rol",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.UNAUTHORIZED,
                            description = "Error de autenticación. Se debe autenticar la petición mediante usuario y contraseña",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.FORBIDDEN,
                            description = "Error de autorización. El usuario autenticado no cuenta con los permisos suficientes",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.INTERNAL_SERVER_ERROR,
                            description = "Error inesperado interno del sistema",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
            })
    @Secured(ROLE_ADMIN)
    @GetMapping("/roles")
    public ResponseEntity<RolesResponse> fetchRoles() {
        List<Privileges> privilegesList = rolesService.findAll();

        final List<RoleResponse> rolesListResponse = privilegesList.stream()
                .distinct()
                .map(RoleResponse::newInstance)
                .collect(Collectors.toList());

        if (rolesListResponse.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(RolesResponse
                .builder()
                .roles(rolesListResponse)
                .build());
    }

    @Operation(
            summary = "Obtener un rol",
            description = "Obtener un rol disponible a partir de su ID",
            tags = SwaggerTags.ROLES,
            parameters = {
                    @Parameter(
                            name = "roleId",
                            description = "ID del rol")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Rol solicitado",
                            content = @Content(
                                    schema = @Schema(implementation = RoleResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NOT_FOUND,
                            description = "El ID provisto no corresponde a ningún rol disponible",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.UNAUTHORIZED,
                            description = "Error de autenticación. Se debe autenticar la petición mediante usuario y contraseña",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.FORBIDDEN,
                            description = "Error de autorización. El usuario autenticado no cuenta con los permisos suficientes",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.INTERNAL_SERVER_ERROR,
                            description = "Error inesperado interno del sistema",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
            })
    @Secured(ROLE_ADMIN)
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<RoleResponse> fetchRole(@PathVariable(name = "roleId") Integer roleId) {
        List<Privileges> rolesList = rolesService.findAll();

        Optional<Privileges> optionalRol = rolesList.stream()
                .distinct()
                .filter(r -> r.getRoleId().equals(roleId))
                .findAny();

        if (optionalRol.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested role with id " + roleId + " does not exists.");

        return ResponseEntity.ok().body(RoleResponse.newInstance(optionalRol.get()));
    }
}
