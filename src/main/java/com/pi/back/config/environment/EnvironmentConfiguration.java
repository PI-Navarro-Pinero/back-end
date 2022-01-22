package com.pi.back.config.environment;

import com.pi.back.config.WeaponryInitializer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Setter
@Component
public class EnvironmentConfiguration {

    private static String baseDir = System.getenv("BASE_DIR");
    private final String outputsDir = "outputs/";
    private final String logsDir = ".logs/";
    private ApplicationContext appContext;

    @Autowired
    public EnvironmentConfiguration(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Bean
    public void makeDirectories() {
        if (baseDir == null || baseDir.isBlank()) {
            SpringApplication.exit(appContext, () -> 0);
            log.error("Environment variable BASE_DIR with path to '{}' file has not been defined.", WeaponryInitializer.YAML_NAME);
            return;
        }

        File parent = new File(baseDir);
        String[] children = {outputsDir, logsDir};

        if (!parent.canWrite()) {
            SpringApplication.exit(appContext, () -> 0);
            log.error("Can't write into {}. Not enough permissions", parent);
            return;
        }

        if (!parent.exists()) {
            if (parent.mkdir())
                log.info("Created base directory: {}", parent);
        } else
            log.info("'{}' directory was already created", parent);

        for (String child : children) {
            File sub = new File(parent, child);
            if (!sub.exists()) {
                if (sub.mkdir())
                    log.info("Created '{}' directory", child);
            } else
                log.info("'{}' subdirectory in '{}' was already created", child, parent);
        }
    }

    public static String getBaseDirectoryPath() {
        return baseDir;
    }
}
