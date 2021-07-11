package com.pi.back.web.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewEmailRequestTest {

    private static final String SMTP_USERNAME = "usuario@gmail.com";
    private static final String SMTP_PASSWORD = "pass";
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;
    private static final String FROM = "maven@test.com";
    private static final String TO = "tspinero@mi.unc.edu.ar";
    private static final String SUBJECT = "Testing";
    private static final String BODY = "Enviado desde test.";

    private static NewEmailRequest emailRequest;

    @BeforeEach
    public void setUp() {
        emailRequest = new NewEmailRequest();

        emailRequest.setFrom(FROM);
        emailRequest.setTo(TO);
        emailRequest.setSubject(SUBJECT);
        emailRequest.setBody(BODY);
        emailRequest.setSmtp_username(SMTP_USERNAME);
        emailRequest.setSmtp_password(SMTP_PASSWORD);
        emailRequest.setSmtp_server(SMTP_SERVER);
        emailRequest.setSmtp_port(SMTP_PORT);
    }

    @Test
    @DisplayName("Comprueba los campos de la request")
    void shouldGetRequest(){
        assertEquals(emailRequest.getFrom(), FROM);
        assertEquals(emailRequest.getTo(), TO);
        assertEquals(emailRequest.getBody(), BODY);
        assertEquals(emailRequest.getSubject(), SUBJECT);
    }
}