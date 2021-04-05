package com.pi.back.web.email;

import com.pi.back.web.SendEmailController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SendEmailControllerTest {

    @InjectMocks
    private SendEmailController sendEmailController;

    @Mock
    private EmailConfig emailConfig;

    @Mock
    private EmailService emailService;

    private NewEmailRequest emailRequest;

    @Before
    public void setUp() {
        String body = "{   \n" +
                "    \"smtp_username\":\"usuario@gmail.com\",\n" +
                "    \"smtp_password\":\"pass\",\n" +
                "    \"smtp_server\":\"smtp.gmail.com\",\n" +
                "    \"smtp_port\":\"587\",\n" +
                "    \"from\": \"maven@test.com\",\n" +
                "    \"to\": \"tspinero@mi.unc.edu.ar\",\n" +
                "    \"subject\": \"Testing\",\n" +
                "    \"body\": \"Enviado desde test.\",  \n" +
                "    \"headers\": {\"\": \"\"},\n" +
                "    \"properties\": {\"\": \"\"}\n" +
                "}";

        emailRequest.setBody(body);
    }

    @Test
    void shouldSendEmail(){
        ResponseEntity<Void> response = sendEmailController.sendEmail(emailRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}