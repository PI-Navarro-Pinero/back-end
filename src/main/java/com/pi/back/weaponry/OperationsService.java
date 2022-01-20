package com.pi.back.weaponry;

import com.pi.back.utils.CommandValidator;
import com.pi.back.weaponry.dto.ExecuteActionDTO;
import com.pi.back.utils.FileSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Slf4j
public class OperationsService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final ProcessesManager processesManager;
    private final WeaponsRepository weaponsRepository;
    private final SystemManager systemManager;

    @Autowired
    public OperationsService(WeaponsRepository weaponsRepository,
                             ProcessesManager processesManager,
                             SystemManager systemManager) {
        this.weaponsRepository = weaponsRepository;
        this.processesManager = processesManager;
        this.systemManager = systemManager;
    }

    private String buildCommand(String commandModel, List<String> parametersList) throws InvalidAttributesException {
        try {
            Optional<String> optionalString = CommandValidator.buildCommand(commandModel, parametersList);

            if (optionalString.isEmpty()) {
                String message = String.format("Command '%s' is unsuitable with parameters '%s'", commandModel, parametersList);
                log.info(message);
                throw new InvalidAttributesException(message);
            }

            return optionalString.get();
        } catch (Exception e) {
            log.error("Unexpected error when building with command model '{}' and parameters {}: {}", commandModel, parametersList, e);
            throw e;
        }
    }

    public File createDirectory(String outputPath) {
        try {
            return systemManager.createDirectory(outputPath);
        } catch (SecurityException e) {
            log.error("Security violation when creating output path file to '{}': {}", outputPath, e);
        } catch (Exception e) {
            log.error("Unexpected error while creating directory at {}: {}", outputPath, e);
        }

        throw new RuntimeException();
    }

    public WeaponProcess executeAction(ExecuteActionDTO dto) throws InvalidAttributesException {
        Weapon weapon = getWeapon(dto.getWeaponId());

        String commandModel = weapon.retrieveAction(dto.getActionId())
                .orElseThrow(() -> {
                    String message = "Weapon '" + weapon.getName() + "' does not contain any action with id " + dto.getActionId();
                    log.info(message);
                    return new InvalidAttributesException(message);
                });

        String command = buildCommand(commandModel, dto.getParameters());

        Consumer<Process> onExitBehavior = p -> {
            try {
                log.info("Calculating checksum for process {}", p.pid());
                systemManager.checksumResults(p);
            } catch (Exception e) {
                log.error("Could not calculate checksum for process {}: {}", p.pid(), e);
            }
        };

        return ActionLauncher.builder().build()
                .setWeapon(weapon)
                .setCommand(command)
                .defineDirectory(this::createDirectory, String.format("%s/%s/%s/stdout",
                        OUTPUTS_DIR, weapon.getName(), LocalDateTime.now(ZoneOffset.UTC)))
                .changeOnExitBehavior(onExitBehavior)
                .registerWeaponProcess(processesManager::insert)
                .execute(this::runCommand);
    }

    public Process runCommand(String command, File outputFile) {
        try {
            Process result = systemManager.execute(command, outputFile);
            log.info("Command '{}' executed successfully", command);
            return result;
        } catch (IOException e) {
            log.info("IO error occurred while executing command '{}': {}", command, e);
        } catch (UnsupportedOperationException e) {
            log.error("OS does not support the creation of process with command '{}'", command);
        } catch (Exception e) {
            log.error("Unexpected error while executing command '{}': {}", command, e);
        }

        throw new RuntimeException();
    }

    public Stream<String> runCommand(String command) throws IOException {
        try {
            Process process = systemManager.execute(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return br.lines();
        } catch (Exception e) {
            throw new IOException("Command '" + command + "' execution failed.");
        }
    }

    public String getProcessPathname(Long pid) throws InvalidAttributesException {
        try {
            String path = processesManager.getProcessStdoutFilePath(pid);
            log.info("Path of process {} is '{}'", pid, path);
            return path;
        } catch (InvalidAttributesException e) {
            log.info(e.getMessage());
            throw e;
        }
    }

    public String getProcessDirectoryPathname(Long pid) throws InvalidAttributesException {
        try {
            String path = processesManager.getProcessDirectoryPath(pid);
            log.info("Directory path of process {} is '{}'", pid, path);
            return path;
        } catch (InvalidAttributesException e) {
            log.info(e.getMessage());
            throw e;
        }
    }

    public void inputToProcess(Long pid, String input) throws InvalidAttributesException {
        try {
            processesManager.writeIntoProcess(pid, input);
        } catch (InvalidAttributesException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error when writing '{}' into process {}: {}", input, pid, e);
            throw e;
        }
    }

    public List<Weapon> getAvailableWeapons() {
        Optional<Weaponry> optionalList = Optional.ofNullable(weaponsRepository.getWeaponsList());

        if (optionalList.isEmpty())
            return List.of();

        return optionalList.get().getWeaponry();
    }

    public Weapon getWeapon(Integer weaponId) throws InvalidAttributesException {
        Optional<Weapon> optionalWeapon = weaponsRepository.findWeapon(weaponId);

        return optionalWeapon.orElseThrow(() -> {
            String errMsg = "Requested weapon with id " + weaponId + " does not exists.";
            log.info(errMsg);
            return new InvalidAttributesException(errMsg);
        });
    }

    public String getConfigurationFilePath(Integer weaponId) throws InvalidAttributesException {
        Weapon weapon = getWeapon(weaponId);
        return getConfigurationFilePathOf(weapon);
    }

    public Map<Long, WeaponProcess> getRunningActions() {
        return processesManager.getAllRunningProcesses();
    }

    public Map<Long, WeaponProcess> getFinalizedActions() {
        return processesManager.getAllTerminatedProcesses();
    }

    public void killRunningAction(Long pid) throws InvalidAttributesException {
        try {
            processesManager.terminate(pid);
        } catch (InvalidAttributesException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public void writeFile(String filePath, String content) throws IOException {
        try {
            systemManager.writeFile(filePath, content);
            log.info("File '{}' has been written with requested content {}.", filePath, content);
        } catch (IOException e) {
            log.error("File '{}' couldn't be written the requested content.", filePath);
            throw e;
        }
    }

    public String readFile(String filePath) throws InvalidAttributesException {
        try {
            return systemManager.readFile(filePath);
        } catch (IOException e) {
            String errMsg = "Invalid file name";
            log.info(errMsg);
            throw new InvalidAttributesException(errMsg);
        } catch (InvalidPathException e) {
            String errMsg = "Provided path '" + filePath + "' could not be processed: " + e;
            log.error(errMsg);
            throw e;
        }
    }

    private String getConfigurationFilePathOf(Weapon weapon) throws InvalidAttributesException {
        File configurationFile = weapon.getConfigFile();

        if (configurationFile == null) {
            String errMsg = "Requested weapon '" + weapon.getName() + "' does not require a configuration file";
            log.info(errMsg);
            throw new InvalidAttributesException(errMsg);
        }

        log.info("Configuration file path for weapon '{}' has been retrieved.", weapon.getName());
        return configurationFile.getAbsolutePath();
    }
}
