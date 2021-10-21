package com.pi.back.services;

import com.pi.back.utils.FileSystem;
import com.pi.back.weaponry.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemService {

    private final String OUTPUTS_DIR = FileSystem.OUTPUTS.getPath();

    private final CommandManager commandManager;
    private final RunningProcessesManager runningProcessesManager;
    private final WeaponsRepository weaponsRepository;
    private final SystemManager systemManager;

    @Autowired
    public SystemService(CommandManager commandManager,
                         WeaponsRepository weaponsRepository,
                         RunningProcessesManager processesManager,
                         SystemManager systemManager) {
        this.commandManager = commandManager;
        this.weaponsRepository = weaponsRepository;
        this.runningProcessesManager = processesManager;
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
        String outputPath = OUTPUTS_DIR + "/" + weapon.getName() + "/" + actionId;
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

        WeaponProcess weaponProcess = WeaponProcess.newInstance(process, weapon, outputFile);
        runningProcessesManager.insert(weaponProcess);

        return weaponProcess;
    }

    public String runCommand(String command) throws IOException {
        BufferedReader br;

        try {
            br = systemManager.execute(command);
        } catch (Exception e) {
            throw new IOException("Command '" + command + "' execution failed.");
        }

        return br.lines().collect(Collectors.joining("\n"));
    }

    public String getProcessPathname(Long pid) throws InvalidAttributesException {
        File file = weaponsRepository.getWeaponsList()
                .getWeaponry().stream()
                .map(Weapon::getName)
                .map(weaponName -> systemManager.findFile(FileSystem.OUTPUTS.getPath() + "/" + weaponName, pid.toString()))
                .filter(Objects::nonNull)
                .findAny()
                .orElseThrow(() -> new InvalidAttributesException("Provided pid " + pid + " is invalid."));

        return file.getAbsolutePath();
    }

    public void inputToProcess(Long pid, String input) {
        OutputStream outputStream = runningProcessesManager.retrieveAll().get(pid).getProcess().getOutputStream();
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
        Optional<String> configurationFilePath = weaponsRepository.getConfigurationFilePath(weaponId);

        if (configurationFilePath.isEmpty())
            throw new IOException("Requested weapon " + weaponId + " does not require configuration file.");

        return configurationFilePath.get();
    }

    public Map<Long, WeaponProcess> getRunningActions() {
        return runningProcessesManager.retrieveAll();
    }

    public void killRunningAction(Long pid) throws InvalidAttributesException {
        try {
            runningProcessesManager.terminate(pid);
        } catch (InvalidAttributesException e) {
            log.error(e.getMessage());
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
