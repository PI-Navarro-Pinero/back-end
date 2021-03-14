package com.pi.back.web;

import com.pi.back.web.email.NewEmailRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SendEmailController {

    @PostMapping("/mailclient")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody NewEmailRequest newEmailRequest) {

        return ResponseEntity.ok().build();
    }
}
