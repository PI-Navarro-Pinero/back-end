package com.pi.back.config;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.Action;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponsRepository;
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
    private final String WEAPONS_DIR = FileSystem.WEAPONS.getPath();

    private final WeaponsRepository weaponsRepository;

    @Autowired
    public WeaponryInitializer(WeaponsRepository weaponsRepository) {
        this.weaponsRepository = weaponsRepository;
    }

    @Bean
    public void initialize() {
        AtomicInteger index = new AtomicInteger();

        try (Stream<Path> weaponsPath = Files.walk(Paths.get(WEAPONS_DIR))) {
            weaponsPath
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        int i = index.getAndIncrement();
                        weaponsRepository.insert(i, loadWeaponFile(filename));
                    });
        } catch (IOException e) {
            log.error("Error when trying to list files from '{}' directory", ACTIONS_DIR);
        }
    }

    private Weapon loadWeaponFile(String fileName) {
        File file = new File(WEAPONS_DIR + "/" + fileName);
        StringBuilder description = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines()
                    .forEach(description::append);
        } catch (Exception e) {
            log.error("Error loading file '{}'", fileName);
        }

        Action actions = loadActionsFile(fileName);

        return Weapon.builder()
                .name(fileName)
                .description(description.toString())
                .actions(actions)
                .build();
    }

    private Action loadActionsFile(String fileName) {
        File file = new File(ACTIONS_DIR + "/" + fileName);
        Map<Integer, String> actionsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.lines()
                    .map(line -> line.split(";"))
                    .forEach(parts -> actionsMap.put(Objects.requireNonNull(Integer.valueOf(parts[0].trim())),
                            Objects.requireNonNull(parts[1].trim())));

        } catch (Exception e) {
            log.error("Error loading file '{}'", fileName);
        }

        return Action.builder().actionsMap(actionsMap).build();
    }
}
