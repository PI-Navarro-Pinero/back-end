package com.pi.back.services;

import com.pi.back.cmd.ActionsManager;
import com.pi.back.cmd.CommandManager;
import com.pi.back.cmd.WeaponsManager;
import com.pi.back.utils.FileSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SystemService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final CommandManager commandManager;
    private final WeaponsManager weaponsManager;
    private final ActionsManager actionsManager;

    @Autowired
    public SystemService(CommandManager commandManager, WeaponsManager weaponsManager, ActionsManager actionsManager) {
        this.commandManager = commandManager;
        this.weaponsManager = weaponsManager;
        this.actionsManager = actionsManager;
    }

    public boolean run(Integer weaponId, Integer actionId, List<String> queryParamsList) throws InvalidAttributesException, FileSystemException, ExecutionException {
        String command = retrieveCommandModel(weaponId, actionId);
        boolean inputIsValid = commandManager.validateUserInput(command, queryParamsList.size());

        if (inputIsValid) {
            command = commandManager.buildCommand(command, queryParamsList);
        } else {
            log.error("Unmatchable model '{}' with list '{}'", queryParamsList, command);
            throw new InvalidAttributesException();
        }

        String outputPath = OUTPUTS_DIR + "/" + getWeaponName(weaponId) + "/" + actionId;
        Process process = execute(command, outputPath);

        return true;
    }

    private String getWeaponName(Integer index) {
        return weaponsManager.queryWeaponsMap(index);
    }

    private String retrieveCommandModel(Integer weaponId, Integer actionId) throws InvalidAttributesException {
        String commandModel = actionsManager.queryActionsMap(weaponId, actionId);

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
}
