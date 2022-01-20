package com.pi.back.web;

import com.pi.back.config.swagger.HttpStatusCodes;
import com.pi.back.config.swagger.SwaggerConfig;
import com.pi.back.config.swagger.SwaggerTags;
import com.pi.back.web.email.EmailConfig;
import com.pi.back.web.email.EmailService;
import com.pi.back.web.email.NewEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static com.pi.back.config.security.Privileges.Roles.ROLE_AGENT;

@Slf4j
@RestController
public class SendEmailController {

    private final EmailService mailer;
    private final EmailConfig emailConfig;

    @Autowired
    public SendEmailController(EmailService mailer, EmailConfig emailConfig) {
        this.mailer = mailer;
        this.emailConfig = emailConfig;
    }

    @Operation(
            hidden = true,
            summary = "Enviar un correo electrónico",
            description = "Enviar un correo electrónico utilizando un servidor SMTP parametrizado en el cuerpo de la petición",
            tags = SwaggerTags.UTILITIES,
            security = @SecurityRequirement(name = SwaggerConfig.BOOKINGS_BASIC_AUTH),
            responses = {
                    @ApiResponse(
                            responseCode = HttpStatusCodes.OK,
                            description = "Correo electrónico enviado",
                            content = @Content(
                                    schema = @Schema(),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = HttpStatusCodes.BAD_REQUEST,
                            description = "Falló el envío del correo electrónico",
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
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody NewEmailRequest newEmailRequest) {
        emailConfig.setSenderConfiguration(newEmailRequest);
        try {
            mailer.send(newEmailRequest, null);
            return ResponseEntity.ok().build();
        } catch (MailException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
