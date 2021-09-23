package com.pi.back.config.environment;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "pinp")
@EnableConfigurationProperties
@Setter
@Slf4j
public class EnvironmentConfiguration {

    private String baseDir;
    private String weaponryDir = "weaponry/";
    private String actionsDir  = "actions/";
    private String outputsDir  = "outputs/";
    private String logsDir     = ".logs/";

    @EventListener(ApplicationReadyEvent.class)
    public void makeDirectories() {

        File parent = new File(baseDir);
        String[] children= {weaponryDir, actionsDir, outputsDir, logsDir};

        if(!parent.isAbsolute()) {
            log.error("Base directory '{}' is not absolute", parent);
        }

        if(!parent.canWrite()) {
            log.error("Can't write to {}. I don't have the right permissions", parent);
        }

        if (!parent.exists()) {
            if (parent.mkdir())
                log.info("Created base directory: {}", parent);
            else
                log.info("'{}' is the base directory.", parent);
        }

        for(String child : children) {
            File sub = new File(parent, child);
            if(!sub.exists()) {
                if (sub.mkdir())
                    log.info("Created '{}' directory", child);
                else
                    log.info("'{}' directory already exists.", child);
            }
        }
    }
}
