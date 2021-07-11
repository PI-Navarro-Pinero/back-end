package com.pi.back.web.email;

import lombok.Getter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Getter
public class EmailConfig {

    private Properties properties;
    private String username;
    private String password;
    private String host;
    private int port;

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setHost(host);
        mailSender.setPort(port);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        if(properties != null)
            properties.forEach((key, value) -> props.setProperty(key.toString(), value.toString()));

        return mailSender;
    }

    public void setSenderConfiguration(NewEmailRequest emailClientConfiguration) {
        username = emailClientConfiguration.getSmtp_username();
        password = emailClientConfiguration.getSmtp_password();
        host = emailClientConfiguration.getSmtp_server();
        port = emailClientConfiguration.getSmtp_port();
        properties = emailClientConfiguration.getProperties();
    }
}
