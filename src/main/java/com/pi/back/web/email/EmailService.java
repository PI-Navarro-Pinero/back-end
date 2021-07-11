package com.pi.back.web.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.Properties;

@Service("emailService")
public class EmailService {
    private final EmailConfig emailConfig;
    private JavaMailSender mailSender;

    @Autowired
    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public void send(NewEmailRequest email, String fileToAttach) throws MailException {
        Properties headers = email.getHeaders();
        this.mailSender = emailConfig.getJavaMailSender();

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
            mimeMessage.setFrom(new InternetAddress(email.getFrom()));
            mimeMessage.setSubject(email.getSubject());
            mimeMessage.setText(email.getBody());

            if(headers != null)
                headers.forEach((key, value) -> {
                    try {
                        mimeMessage.setHeader(key.toString(), value.toString());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });

            // TODO implement attachments functionality
            if(fileToAttach != null) {
                FileSystemResource file = new FileSystemResource(new File(fileToAttach));
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.addAttachment("logo.jpg", file);
            }
        };

        mailSender.send(preparator);
    }
}