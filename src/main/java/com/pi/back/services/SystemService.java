package com.pi.back.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

@Service
@Slf4j
public class SystemService {

    private static final String BASE_DIR = System.getenv("BASE_DIR");
    private static final String OUTPUTS_DIR = BASE_DIR + "outputs/";
    private static final String ACTIONS_DIR = BASE_DIR + "actions/";

    Map<Integer, Map<Integer, String>> actionsFilesMap;

    public SystemService() {
        actionsFilesMap = new HashMap<>();
    }

    public String retrieveCommandModel(Integer weaponId, Integer actionId) {
        try {
            return actionsFilesMap.get(weaponId).get(actionId);
        } catch (Exception e) {
            log.error("Error when trying to retrieve command model with weaponId {} and actionId {}", weaponId, actionId);
            return null;
        }
    }

    public boolean run(String cmd, String weaponId, String actionId) {
        final String OUTPUT = OUTPUTS_DIR + weaponId + "/" + actionId;

        ProcessBuilder procBuilder = new ProcessBuilder(/*cmd*/"ls","-l", "-a", "--author");

        try {
            File action = new File(OUTPUT);
            action.getParentFile().mkdirs();
            procBuilder.redirectOutput(action);

            Process proc = procBuilder.start();

            log.info("Command '{}' executed", cmd);
            return true;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    @PostConstruct
    public void listFilesFromFolder() {
        AtomicInteger index = new AtomicInteger();

        try (Stream<Path> paths = Files.walk(Paths.get(ACTIONS_DIR))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> actionsFilesMap.put(index.getAndIncrement(),
                            hashMapFromTextFile(file.getFileName().toString())));
        } catch (IOException e) {
            log.error("Error when trying to list files from '{}' directory", ACTIONS_DIR);
        }
    }

    private Map<Integer, String> hashMapFromTextFile(String fileName) {
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

    private String arrayToString(String[] str) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String s : str) {
            if (!first)
                builder.append(' ');

            builder.append(s);
            first = false;
        }

        return builder.toString();
    }
}
