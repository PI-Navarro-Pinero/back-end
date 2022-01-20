package com.pi.back.mail.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Properties;

@Data
public class NewEmailRequest {
    @NotNull
    private String smtp_username;
    @NotNull
    private String smtp_password;
    @NotNull
    private String smtp_server;
    @NotNull
    private int smtp_port;

    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private String subject;
    @NotNull
    private String body;
    @Schema(nullable = true)
    private Properties headers;
    @Schema(nullable = true)
    private Properties properties;
}
