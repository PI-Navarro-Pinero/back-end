package com.pi.back.web.email;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NewEmailRequest {
    @NotNull
    private String smtp_username;
    @NotNull
    private String smtp_password;
    @NotNull
    private String smtp_server;
    @NotNull
    private String smtp_port;

    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private String subject;
    @NotNull
    private String body;

    private String adv_messageContentType;
    private String adv_messageFile;
    private String adv_messageHeader;
    private String adv_replyTo;
    private String adv_username;
    private String adv_tls;
    private String adv_messageFormat;
    private String adv_messageCharset;
    private String adv_timeout;
    private String password;
    private String fqdn;
}
