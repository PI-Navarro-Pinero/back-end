package com.pi.back.services;

import com.pi.back.cmd.CommandManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@Slf4j
public class SystemService {

    private static final String BASE_DIR = System.getenv("BASE_DIR");
    private static final String OUTPUTS_DIR = BASE_DIR + "outputs/";
    private static final String ACTIONS_DIR = BASE_DIR + "actions/";

    Map<Integer, Map<Integer, String>> actionsFilesMap;

    private final CommandManager commandManager;

    @Autowired
    public SystemService(CommandManager commandManager) {
        actionsFilesMap = new HashMap<>();
        this.commandManager = commandManager;
        listFilesFromFolder();
    }

    public boolean run(Integer weaponId, Integer actionId, List<String> queryParamsList) throws InvalidAttributesException, FileSystemException, ExecutionException {
        String command = retrieveCommandModel(weaponId, actionId);

        boolean inputIsValid = commandManager.validateUserInput(command, queryParamsList.size());

        if (inputIsValid) {
            command = commandManager.buildCommand(command, queryParamsList);
        } else {
            log.error("");
            throw new InvalidAttributesException();
        }

        Process process = execute(command, OUTPUTS_DIR + weaponId);
        return true;
    }

    public String retrieveCommandModel(Integer weaponId, Integer actionId) throws InvalidAttributesException {
        String commandModel = actionsFilesMap.get(weaponId).get(actionId);

        if (commandModel.isEmpty())
            throw new InvalidAttributesException();

        return commandModel;
    }

    private Process execute(String command, String outputPath) throws FileSystemException, ExecutionException {
        File outputFile;

        try {
            outputFile = createOutputDirectory(outputPath);
        } catch (Exception e) {
            log.error("Error creating output path file to '{}'", outputPath);
            throw new FileSystemException("Error creating output path file");
        }

    public boolean run(String cmd, String weaponId, String actionId) {
        final String OUTPUT = OUTPUTS_DIR + weaponId + "/" + actionId;
        List<String> splitCommand = List.of(command.split(" "));

        ProcessBuilder procBuilder = new ProcessBuilder(splitCommand);

        try {
            Process proc = procBuilder.redirectOutput(outputFile).start();
            log.info("Command '{}' executed successfully", command);
            return proc;
        } catch (Exception e) {
            log.error("Error executing command '{}'", command);
            log.debug("Exception '{}' thrown when executing command '{}': '{}'", e.getClass(), command, e.getCause());
            throw new ExecutionException(e);
        }
    }

    private File createOutputDirectory(String outputPath) {
        File outputFile = new File(outputPath);
        boolean directoryIsCreated = outputFile.getParentFile().mkdirs();

        if (directoryIsCreated)
            log.info("Directory '{}' has been created", outputPath);

        return outputFile;
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

    private void listFilesFromFolder() {
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
}
