package com.pi.back.web;

import com.pi.back.config.swagger.HttpStatusCodes;
import com.pi.back.config.swagger.SwaggerConfig;
import com.pi.back.config.swagger.SwaggerTags;
import com.pi.back.services.OperationsService;
import com.pi.back.utils.ExecuteActionDTO;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import com.pi.back.web.weaponry.ActionOutputResponse;
import com.pi.back.web.weaponry.ActionResponse;
import com.pi.back.web.weaponry.ActionsResponse;
import com.pi.back.web.weaponry.WeaponResponse;
import com.pi.back.web.weaponry.WeaponsResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_AGENT;

@RestController
@Slf4j
public class WeaponryController {

    private final OperationsService operationsService;

    @Autowired
    public WeaponryController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @Operation(
            summary = "Listado de herramientas disponibles",
            description = "Obtener el listado completo de herramientas disponibles",
            tags = SwaggerTags.WEAPONRY,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado completo de herramientas disponibles",
                            content = @Content(
                                    schema = @Schema(implementation = WeaponsResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.NO_CONTENT,
                            description = "No hay ninguna herramienta disponible",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry")
    public ResponseEntity<WeaponsResponse> fetchWeaponry() {
        try {
            List<Weapon> weaponMap = operationsService.getAvailableWeapons();

            AtomicInteger index = new AtomicInteger();
            final List<WeaponResponse> weaponsListResponse = weaponMap.stream()
                    .map(weapon -> WeaponResponse.newInstance(index.getAndIncrement(), weapon))
                    .collect(Collectors.toList());

            if (weaponsListResponse.isEmpty())
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(WeaponsResponse
                    .builder()
                    .weaponry(weaponsListResponse)
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Obtener una herramientas disponible",
            description = "Obtener una herramienta disponible a partir de su ID",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "weaponId",
                            description = "ID de la herramienta")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Detalles de la herramienta",
                            content = @Content(
                                    schema = @Schema(implementation = WeaponResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El ID provisto no corresponde a ninguna herramienta disponible",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry/{weaponId}")
    public ResponseEntity<WeaponResponse> fetchWeaponActions(@PathVariable(name = "weaponId") Integer weaponId) {
        try {
            Weapon weapon = operationsService.getWeapon(weaponId);
            return ResponseEntity.ok(WeaponResponse.newInstance(weaponId, weapon));
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Obtener archivo de configuración",
            description = "Obtener el contenido del archivo de configuración de una herramienta a partir de su ID",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "weaponId",
                            description = "ID de la herramienta"),
                    @Parameter(
                            name = "encode",
                            description = "Codificar la respuesta en Base64")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Contenido del archivo de configuración",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.TEXT_PLAIN_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El ID provisto no corresponde a ninguna herramienta disponible o no requiere archivo de configuración",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry/{weaponId}/configuration-file")
    public ResponseEntity<String> getConfigurationFile(@PathVariable(name = "weaponId") Integer weaponId,
                                                       @RequestParam(value = "encode", required = false, defaultValue = "0") Boolean encode) {
        try {
            String pathname = operationsService.getConfigurationFilePath(weaponId);
            String result = operationsService.runCommand("cat " + pathname)
                    .collect(Collectors.joining("\n"));
            if (encode)
                result = new String(Base64.getEncoder().encode(result.getBytes()));
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Actualizar archivo de configuración",
            description = "Actualizar el contenido del archivo de configuración de una herramienta a partir de su ID",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "weaponId",
                            description = "ID de la herramienta"),
                    @Parameter(
                            name = "encoded",
                            description = "Codificar el contenido en Base64")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Contenido del archivo de configuración actualizado correctamente",
                            content = @Content(
                                    schema = @Schema())),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El ID provisto no corresponde a ninguna herramienta disponible o no requiere archivo de configuración",
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
    @Secured(ROLE_AGENT)
    @PutMapping("/weaponry/{weaponId}/configuration-file")
    public ResponseEntity<Void> setConfigurationFile(@PathVariable(name = "weaponId") Integer weaponId,
                                                     @RequestBody String configurationFile,
                                                     @RequestParam(required = false) boolean encoded) {
        try {
            String pathname = operationsService.getConfigurationFilePath(weaponId);
            if (encoded)
                configurationFile = new String(Base64.getDecoder().decode(configurationFile.getBytes()));
            operationsService.writeFile(pathname, configurationFile);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Lanzar una accion",
            description = "Lanzar acción de una herramienta disponible a partir de sus IDs",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "weaponId",
                            description = "ID de la herramienta"),
                    @Parameter(
                            name = "actionId",
                            description = "ID de la acción"),
                    @Parameter(
                            name = "parameters",
                            description = "Parametros necesarios para lanzar la acción si esta lo requiere"),
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Detalles asociados a la acción lanzada",
                            content = @Content(
                                    schema = @Schema(implementation = ActionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "Algún ID o los parámetros provistos son inválidos para el lanzamiento de la acción",
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
    @Secured(ROLE_AGENT)
    @PostMapping("/weaponry/{weaponId}/actions/{actionId}")
    public ResponseEntity<ActionResponse> executeAction(@PathVariable(name = "weaponId") Integer weaponId,
                                                        @PathVariable(name = "actionId") Integer actionId,
                                                        @RequestParam(name = "parameters", required = false) List<String> parameters) {
        try {
            ExecuteActionDTO dto = ExecuteActionDTO.builder()
                    .weaponId(weaponId)
                    .actionId(actionId)
                    .parameters(parameters == null ? List.of() : parameters)
                    .build();
            WeaponProcess result = operationsService.executeAction(dto);
            return ResponseEntity.ok().body(ActionResponse.newInstance(result));
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Acciones lanzadas",
            description = "Listado completo de acciones lanzadas",
            tags = SwaggerTags.WEAPONRY,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado de acciones lanzadas",
                            content = @Content(
                                    schema = @Schema(implementation = ActionsResponse.class),
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
    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions")
    public ResponseEntity<ActionsResponse> getLaunchedActions() {
        try {
            List<ActionResponse> finalizedActionResponseList = operationsService.getFinalizedActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            List<ActionResponse> runningActionResponseList = operationsService.getRunningActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            ActionsResponse response = ActionsResponse.builder()
                    .finalizedActions(finalizedActionResponseList)
                    .runningActions(runningActionResponseList)
                    .build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Obtener resultado de acción lanzada",
            description = "Realizar la lectura de una acción lanzada a partir de su PID",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "pid",
                            description = "ID de la acción lanzada"),
                    @Parameter(
                            name = "lines",
                            description = "Cantidad de lineas de texto que se desean obtener (contando desde la más reciente). " +
                                    "Ingresar 0 para lectura completa")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Lectura de la acción lanzada",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.TEXT_PLAIN_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El PID provisto no está asociado a ninguna acción lanzada",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.SERVICE_UNAVAILABLE,
                            description = "Error al procesar la lectura del recurso",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/stdout")
    public ResponseEntity<String> readActionStdout(@PathVariable(name = "pid") Long pid,
                                                   @RequestParam(name = "lines", required = false) Integer lines) {
        try {
            String pathname = operationsService.getProcessPathname(pid);
            String command = String.format("tail %s %s",
                    (lines == null ? ("+0") : ("-" + lines)),
                    pathname);
            String result = operationsService.runCommand(command)
                    .collect(Collectors.joining("\n"));
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (IOException e) {
            log.error("Command execution error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Obtener archivos generados de acción lanzada",
            description = "Realizar la lectura del listado de archivos generados por una acción lanzada a partir de su PID",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "pid",
                            description = "ID de la acción lanzada")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado de archivos generados por la acción lanzada",
                            content = @Content(
                                    schema = @Schema(implementation = ActionOutputResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El PID provisto no está asociado a ninguna acción lanzada",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.SERVICE_UNAVAILABLE,
                            description = "Error al procesar la lectura del recurso",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files")
    public ResponseEntity<ActionOutputResponse> getActionOutput(@PathVariable(name = "pid") Long pid) {
        try {
            String command = String.format("ls %s", operationsService.getProcessDirectoryPathname(pid));
            List<String> result = operationsService.runCommand(command).collect(Collectors.toList());

            if (result.isEmpty())
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(ActionOutputResponse.newInstance(result));
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (IOException e) {
            log.error("Command execution error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Leer contenido de un archivo generado por una acción",
            description = "Realizar la lectura del contenido de un archivo generado por una acción lanzada",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "pid",
                            description = "ID de la acción lanzada"),
                    @Parameter(
                            name = "fileName",
                            description = "Nombre del archivo")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Contenido del archivo",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.TEXT_PLAIN_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El PID no está asociado a ninguna acción lanzada o el nombre del archivo es inválido",
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
    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files/{fileName}")
    public ResponseEntity<String> readActionOutputFile(@PathVariable(name = "pid") Long pid,
                                                       @PathVariable(name = "fileName") String fileName) {
        try {
            String pathname = String.format("%s/%s", operationsService.getProcessDirectoryPathname(pid), fileName);
            String result = operationsService.readFile(pathname);
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Acciones lanzadas activas",
            description = "Listado de acciones lanzadas en estado de ejecución",
            tags = SwaggerTags.WEAPONRY,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Listado de acciones lanzadas en ejecución",
                            content = @Content(
                                    schema = @Schema(implementation = ActionsResponse.class),
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
    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/active")
    public ResponseEntity<ActionsResponse> getCurrentRunningActions() {
        try {
            List<ActionResponse> actionResponseList = operationsService.getRunningActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            ActionsResponse response = ActionsResponse.builder()
                    .runningActions(actionResponseList)
                    .build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Detener una acción activa",
            description = "Finalizar la ejecución de una acción lanzada que se encuentra activa",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "pid",
                            description = "ID de la acción activa")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Acción finalizada",
                            content = @Content(
                                    schema = @Schema())),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El PID provisto no está asociado a ninguna acción activa",
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
    @Secured(ROLE_AGENT)
    @DeleteMapping("/launched-actions/active/{pid}/terminate")
    public ResponseEntity<Void> killCurrentRunningAction(@PathVariable(name = "pid") Long pid) {
        try {
            operationsService.killRunningAction(pid);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Escribir por stdin de una acción activa",
            description = "Ingresar datos a un accion en estado de ejecución a través del stdin ",
            tags = SwaggerTags.WEAPONRY,
            parameters = {
                    @Parameter(
                            name = "pid",
                            description = "ID de la acción activa"),
                    @Parameter(
                            name = "stdin",
                            description = "Texto a transferir")
            },
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Los datos fueron escritos",
                            content = @Content(
                                    schema = @Schema())),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.PRECONDITION_FAILED,
                            description = "El PID provisto no está asociado a ninguna acción activa",
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
    @Secured(ROLE_AGENT)
    @PutMapping("/launched-actions/active/{pid}/stdin")
    public ResponseEntity<Void> inputIntoRunningAction(@PathVariable(name = "pid") Long pid,
                                                       @RequestParam(name = "stdin", required = false) String input) {
        try {
            operationsService.inputToProcess(pid, input);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

