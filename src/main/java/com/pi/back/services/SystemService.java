package com.pi.back.services;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.CommandManager;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SystemService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final CommandManager commandManager;
    private final WeaponsRepository weaponsRepository;

    @Autowired
    public SystemService(CommandManager commandManager, WeaponsRepository weaponsRepository) {
        this.commandManager = commandManager;
        this.weaponsRepository = weaponsRepository;
    }

    public boolean run(Integer weaponId, Integer actionId, List<String> queryParamsList) throws InvalidAttributesException, ExecutionException {
        String command = retrieveCommandModel(weaponId, actionId);
        boolean inputIsValid = commandManager.validateUserInput(command, queryParamsList);

        if (inputIsValid) {
            command = commandManager.buildCommand(command, queryParamsList);
        } else {
            String errMsg = "Requested action cannot be executed with provided variables.";
            log.error(errMsg);
            throw new InvalidAttributesException(errMsg);
        }

        String outputPath = OUTPUTS_DIR + "/" + getWeaponName(weaponId) + "/" + actionId;

        try {
            Process process = execute(command, outputPath);
        } catch (Exception e) {
            throw new ExecutionException("Command '" + command + "' execution failed.", e);
        }

        return true;
    }

    public List<Weapon> getAvailableWeapons() {
        return weaponsRepository.getWeaponsList().getWeaponry();
    }

    public Weapon getWeapon(Integer weaponId) throws InvalidAttributesException {
        Optional<Weapon> optionalWeapon = weaponsRepository.findWeapon(weaponId);

        return optionalWeapon.orElseThrow(() ->
                new InvalidAttributesException("Requested weapon with id " + weaponId + " does not exists."));
    }

    private String getWeaponName(Integer index) {
        return weaponsRepository.getWeaponName(index);
    }

    private String retrieveCommandModel(Integer weaponId, Integer actionId) throws InvalidAttributesException {
        Optional<String> commandModel = weaponsRepository.getActionModel(weaponId, actionId);

        if (commandModel.isPresent())
            return commandModel.get();

        String errMsg = "Provided weaponId '" + weaponId + "' or actionId '" + actionId + "' are invalid.";
        log.error(errMsg);
        throw new InvalidAttributesException(errMsg);
    }

    private Process execute(String command, String outputPath) throws IOException {
        File outputFile;

        try {
            outputFile = createOutputDirectory(outputPath);
        } catch (Exception e) {
            log.error("Error creating output path file to '{}'", outputPath);

            throw e;
        }

        List<String> splitCommand = List.of(command.split(" "));
        ProcessBuilder procBuilder = new ProcessBuilder(splitCommand);
        Process proc;
        try {
            proc = procBuilder.redirectOutput(outputFile).start();
            log.info("Command '{}' executed successfully", command);
        } catch (Exception e) {
            log.error("Error executing command '{}'", command);
            log.debug("Exception '{}' thrown when executing command '{}': '{}'", e.getClass(), command, e.getCause());

            throw e;
        }

        return proc;
    }

    private File createOutputDirectory(String outputPath) {
        File outputFile = new File(outputPath);
        boolean directoryIsCreated = outputFile.getParentFile().mkdirs();

        if (directoryIsCreated)
            log.info("Directory '{}' has been created", outputPath);

        return outputFile;
    }
}
