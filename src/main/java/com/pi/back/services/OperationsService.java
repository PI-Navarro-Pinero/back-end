package com.pi.back.services;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class OperationsService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final CommandValidator commandValidator;
    private final ProcessesManager processesManager;
    private final WeaponsRepository weaponsRepository;
    private final SystemManager systemManager;

    @Autowired
    public OperationsService(CommandValidator commandValidator,
                             WeaponsRepository weaponsRepository,
                             ProcessesManager processesManager,
                             SystemManager systemManager) {
        this.commandValidator = commandValidator;
        this.weaponsRepository = weaponsRepository;
        this.processesManager = processesManager;
        this.systemManager = systemManager;
    }

    public WeaponProcess runAction(Integer weaponId, Integer actionId, List<String> queryParamsList) throws InvalidAttributesException, IOException {
        Weapon weapon = getWeapon(weaponId);
        String command = getCommandModelOf(weapon, actionId);

        try {
            command = commandValidator.buildCommand(command, queryParamsList);
        } catch (InvalidAttributesException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error when building command '{}' with parameters {}: ", command, queryParamsList, e);
            throw e;
        }

        String outputPath = OUTPUTS_DIR + "/" + weapon.getName() + "/" + LocalDateTime.now() + "/" + actionId;
        Process process = null;
        File outputFile;
        try {
            outputFile = systemManager.createDirectory(outputPath);
            process = systemManager.execute(command, outputFile);
            outputFile = systemManager.renameFile(outputFile, String.valueOf(process.pid()));
        } catch (Exception e) {
            if (process != null)
                process.destroy();
            throw new IOException("Command '" + command + "' execution failed.");
        }

        WeaponProcess weaponProcess = new WeaponProcess(process, weapon, outputFile);
        processesManager.insert(weaponProcess);

        return weaponProcess;
    }

    public Stream<String> runCommand(String command) throws IOException {
        try {
            BufferedReader br = systemManager.execute(command);
            return br.lines();
        } catch (Exception e) {
            throw new IOException("Command '" + command + "' execution failed.");
        }
    }

    public String getProcessPathname(Long pid) throws InvalidAttributesException {
        try {
            String path = processesManager.getProcessPath(pid);
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
        OutputStream outputStream = processesManager.getRunningProcess(pid).getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(input);
        printWriter.flush();
    }

    public List<Weapon> getAvailableWeapons() {
        return weaponsRepository.getWeaponsList().getWeaponry();
    }

    public Weapon getWeapon(Integer weaponId) throws InvalidAttributesException {
        Optional<Weapon> optionalWeapon = weaponsRepository.findWeapon(weaponId);

        return optionalWeapon.orElseThrow(() -> {
            String errMsg = "Requested weapon with id " + weaponId + " does not exists.";
            log.info(errMsg);
            return new InvalidAttributesException(errMsg);
        });
    }

    public String getConfigurationFilePath(Integer weaponId) throws IOException, InvalidAttributesException {
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

    private String getCommandModelOf(Weapon weapon, Integer actionId) throws InvalidAttributesException {
        try {
            return weapon.getActions().get(actionId);
        } catch (IndexOutOfBoundsException e) {
            String errMsg = "Weapon '" + weapon.getName() + "' does not contain any action with id " + actionId;
            log.info(errMsg);
            throw new InvalidAttributesException(errMsg);
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
