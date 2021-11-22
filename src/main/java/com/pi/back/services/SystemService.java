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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final CommandManager commandManager;
    private final ProcessesManager processesManager;
    private final WeaponsRepository weaponsRepository;
    private final SystemManager systemManager;

    @Autowired
    public SystemService(CommandManager commandManager,
                         WeaponsRepository weaponsRepository,
                         ProcessesManager processesManager,
                         SystemManager systemManager) {
        this.commandManager = commandManager;
        this.weaponsRepository = weaponsRepository;
        this.processesManager = processesManager;
        this.systemManager = systemManager;
    }

    public WeaponProcess runAction(Integer weaponId, Integer actionId, List<String> queryParamsList) throws InvalidAttributesException, ExecutionException, IOException {
        String command = retrieveCommandModel(weaponId, actionId);

        try {
            command = commandManager.buildCommand(command, queryParamsList);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            log.error(errMsg);
            throw new InvalidAttributesException(errMsg);
        }

        Weapon weapon = getWeapon(weaponId);
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

    public String runCommand(String command) throws IOException {
        try {
            BufferedReader br = systemManager.execute(command);
            return br.lines().collect(Collectors.joining("\n"));
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

        return optionalWeapon.orElseThrow(() ->
                new InvalidAttributesException("Requested weapon with id " + weaponId + " does not exists."));
    }

    public String getConfigurationFilePath(Integer weaponId) throws IOException, InvalidAttributesException {
        try {
            String configurationFilePath = weaponsRepository.getConfigurationFilePath(weaponId);
            log.info("Configuration file path for weapon {} has been retrieved.", weaponId);
            return configurationFilePath;
        } catch (InvalidAttributesException e) {
            log.info(e.getMessage());
            throw e;
        }
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

    private String retrieveCommandModel(Integer weaponId, Integer actionId) throws InvalidAttributesException {
        Optional<String> commandModel = weaponsRepository.getActionModel(weaponId, actionId);

        if (commandModel.isPresent())
            return commandModel.get();

        String errMsg = "Provided weaponId '" + weaponId + "' or actionId '" + actionId + "' are invalid.";
        log.error(errMsg);
        throw new InvalidAttributesException(errMsg);
    }
}
