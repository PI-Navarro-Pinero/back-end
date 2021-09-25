package com.pi.back.config;

import com.pi.back.cmd.ActionsManager;
import com.pi.back.cmd.WeaponsManager;
import com.pi.back.utils.FileSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class WeaponryInitializer {

    private final String ACTIONS_DIR = FileSystem.ACTIONS.getPath();

    private final WeaponsManager weaponsManager;
    private final ActionsManager actionsManager;

    @Autowired
    public WeaponryInitializer(WeaponsManager weaponsManager, ActionsManager actionsManager) {
        this.weaponsManager = weaponsManager;
        this.actionsManager = actionsManager;
    }

    @Bean
    public void initialize() {
        AtomicInteger index = new AtomicInteger();

        try (Stream<Path> paths = Files.walk(Paths.get(ACTIONS_DIR))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        int i = index.getAndIncrement();
                        actionsManager.updateActionsMap(i, loadActionsFile(filename));
                        weaponsManager.updateWeaponsMap(i, filename);
                    });
        } catch (IOException e) {
            log.error("Error when trying to list files from '{}' directory", ACTIONS_DIR);
        }
    }

    private Map<Integer, String> loadActionsFile(String fileName) {
        File file = new File(ACTIONS_DIR + "/" + fileName);
        Map<Integer, String> actionsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines()
                    .map(line -> line.split(";"))
                    .forEach(parts -> actionsMap.put(Objects.requireNonNull(Integer.valueOf(parts[0].trim())),
                            Objects.requireNonNull(parts[1].trim())));

        } catch (Exception e) {
            log.error("Error when trying to load file content '{}' into hash map", fileName);
        }

        return actionsMap;
    }
}
