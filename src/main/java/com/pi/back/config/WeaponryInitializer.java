package com.pi.back.config;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.Weaponry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Configuration
@Slf4j
public class WeaponryInitializer {

    private final String BASE_DIR = FileSystem.BASEDIR.getPath();

    @Bean
    public Weaponry loadWeaponsYaml() {
        String fileToOpen = BASE_DIR + "/weapons.yaml";
        File file;

        try {
            file = new File(fileToOpen);
        } catch (Exception e) {
            log.error("Error opening file '{}': {}", fileToOpen, e.getMessage());
            return null;
        }

        Yaml yaml = new Yaml(new Constructor(Weaponry.class));
        return loadYaml(file, yaml);
    }

    protected Weaponry loadYaml(File file, Yaml yaml) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            return yaml.load(br);
        } catch (Exception e) {
            log.error("Error loading file: {}", e.getMessage());
            return null;
        }
    }
}
