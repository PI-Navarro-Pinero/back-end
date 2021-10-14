package com.pi.back.weaponry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.naming.directory.InvalidAttributesException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RunningProcessesManager {

    private Map<Long, WeaponProcess> runningProcessesMap;

    public RunningProcessesManager() {
        this.runningProcessesMap = new HashMap<>();
    }

    public void insert(WeaponProcess newProcess) {
        if (newProcess != null && newProcess.isAlive())
            runningProcessesMap.put(newProcess.pid(), newProcess);
    }

    public Map<Long, WeaponProcess> retrieveAll() {
        cleanDeadProcesses();

        return runningProcessesMap;
    }

    public void terminate(Long pid) throws InvalidAttributesException {
        cleanDeadProcesses();

        Optional<WeaponProcess> optionalWeaponProcess = retrieve(pid);
        if (optionalWeaponProcess.isEmpty())
            throw new InvalidAttributesException("Provided pid is not valid.");

        optionalWeaponProcess.get().terminateProcess();
    }

    public String path(Long pid) throws InvalidAttributesException {
        cleanDeadProcesses();

        Optional<WeaponProcess> optionalWeaponProcess = retrieve(pid);
        if (optionalWeaponProcess.isEmpty())
            throw new InvalidAttributesException("Provided pid is not valid.");

        return optionalWeaponProcess.get().getPathname();
    }

    private void cleanDeadProcesses() {
        runningProcessesMap = runningProcessesMap.entrySet().stream()
                .filter(p -> p.getValue().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Optional<WeaponProcess> retrieve(Long pid) {
        cleanDeadProcesses();

        return Optional.ofNullable(runningProcessesMap.get(pid));
    }
}
