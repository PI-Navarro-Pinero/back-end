package com.pi.back.config;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.Weaponry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Slf4j
@Component
public class WeaponryInitializer {

    public static final String YAML_NAME = "weapons.yaml";
    private final String BASE_DIR = FileSystem.BASEDIR.getPath();
    private ApplicationContext appContext;

    @Autowired
    public WeaponryInitializer(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Bean
    @DependsOn("makeDirectories")
    public Weaponry loadWeaponsYaml() {
        String filePath = BASE_DIR + "/" + YAML_NAME;
        File fileToOpen = new File(filePath);

        if (!fileToOpen.isFile()) {
            SpringApplication.exit(appContext, () -> 0);
            log.error("File '{}' has not been found in '{}'", YAML_NAME, BASE_DIR);
            return null;
        }

        Yaml yaml = new Yaml(new Constructor(Weaponry.class));
        return loadYaml(fileToOpen, yaml);
    }

    protected Weaponry loadYaml(File file, Yaml yaml) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Weaponry loaded = yaml.load(br);
            log.info("Weaponry has been successfully loaded: {}", loaded.toString());
            return loaded;
        } catch (Exception e) {
            return null;
        }
    }
}
