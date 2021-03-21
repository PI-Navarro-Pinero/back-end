package com.pi.back.web;

import com.pi.back.web.email.EmailConfig;
import com.pi.back.web.email.EmailService;
import com.pi.back.web.email.NewEmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SendEmailController {

    private final EmailService mailer;
    private final EmailConfig emailConfig;

    @Autowired
    public SendEmailController(EmailService mailer, EmailConfig emailConfig) {
        this.mailer = mailer;
        this.emailConfig = emailConfig;
    }

    @PostMapping("/mailclient")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody NewEmailRequest newEmailRequest) {
        emailConfig.setSenderConfiguration(newEmailRequest);
        try {
            mailer.send(newEmailRequest, null);
        }
        catch (MailException mailException) {
            System.err.println(mailException.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
