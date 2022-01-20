package com.pi.back.users;

import com.pi.back.config.swagger.HttpStatusCodes;
import com.pi.back.config.swagger.SwaggerConfig;
import com.pi.back.config.swagger.SwaggerTags;
import com.pi.back.users.dto.UserDTO;
import com.pi.back.users.model.UserRequest;
import com.pi.back.users.model.UserResponse;
import com.pi.back.users.model.UsersResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.directory.InvalidAttributesException;
import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_ADMIN;

@RestController
@Slf4j
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @Operation(
            summary = "Listado de usuarios registrados",
            description = "Obtener el listado completo de usuarios registrados en la base de datos",
            tags = SwaggerTags.USERS,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado de usuarios registrados",
                            content = @Content(
                                    schema = @Schema(implementation = UsersResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NO_CONTENT,
                            description = "No se encontró ningún usuario registrado",
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
    @GetMapping("/users")
    public ResponseEntity<UsersResponse> fetchUsers() {
        List<User> usersList = usersService.findAll();

        final List<UserResponse> usersListResponse = usersList.stream()
                .map(UserResponse::newInstance)
                .collect(Collectors.toList());

        if (usersListResponse.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(UsersResponse
                .builder()
                .users(usersListResponse)
                .build());
    }

    @Operation(
            summary = "Detalles de un usuario registrado",
            description = "Obtener los detalles de un usuario registrado en la base de datos mediante su ID",
            tags = SwaggerTags.USERS,
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Detalles del usuario solicitado",
                            content = @Content(
                                    schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NOT_FOUND,
                            description = "El ID provisto no corresponde a ningún usuario registrado",
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
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> fetchUser(@PathVariable(name = "userId") Integer userId) {
        try {
            User user = usersService.findUser(userId);
            final UserResponse userResponse = UserResponse.newDetailedInstance(user);
            return ResponseEntity.ok(userResponse);
        } catch (NoSuchElementException noSuchElementException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Crear nuevo usuario",
            description = "Registrar un nuevo usuario en la base de datos",
            tags = SwaggerTags.USERS,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Usuario creado",
                            content = @Content(
                                    schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El nombre de usuario y/o licencia ya pertencen a un usuario registrado",
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
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        try {
            User createdUser = usersService.create(request);
            return ResponseEntity.ok(UserResponse.newDetailedInstance(createdUser));
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Modificar un usuario",
            description = "Modificar un usuario registrado en la base de datos",
            tags = SwaggerTags.USERS,
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario a modificar")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Usuario modificado",
                            content = @Content(
                                    schema = @Schema(implementation = UserResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "ID inválido, o nombre de usuario/licencia pertencen a un usuario ya registrado",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NOT_FOUND,
                            description = "El usuario que se desea modificar no existe",
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
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest request,
                                                   @PathVariable(name = "userId") Integer userId) {
        UserDTO dto = UserDTO.builder()
                .id(userId)
                .fullname(request.getFullname())
                .license(request.getLicense())
                .username(request.getUsername())
                .privileges(request.getPrivileges())
                .build();
        try {
            User updatedUser = usersService.update(dto);
            return ResponseEntity.ok(UserResponse.newDetailedInstance(updatedUser));
        } catch (InvalidParameterException | InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Eliminar un usuario",
            description = "Eliminar un usuario registrado en la base de datos",
            tags = SwaggerTags.USERS,
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID del usuario a eliminar")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Usuario eliminado",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El usuario solicitado no puede ser eliminado",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NOT_FOUND,
                            description = "ID provisto no pertenece a ningún usuario registrado",
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
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable(name = "userId") Integer userId) {
        try {
            usersService.delete(userId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException noSuchElementException) {
            return ResponseEntity.noContent().build();
        } catch (InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
