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
public class ProcessesManager {

    private Map<Long, WeaponProcess> processesMap;

    public ProcessesManager() {
        this.processesMap = new HashMap<>();
    }

    public void insert(WeaponProcess newProcess) {
        if (newProcess != null && newProcess.isAlive())
            processesMap.put(newProcess.pid(), newProcess);
    }

    public Map<Long, WeaponProcess> retrieveAll() {
        cleanDeadProcesses();

        return processesMap;
    }

    public void terminate(Long pid) throws InvalidAttributesException {
        cleanDeadProcesses();

        Optional<WeaponProcess> optionalWeaponProcess = retrieve(pid);
        if (optionalWeaponProcess.isEmpty())
            throw new InvalidAttributesException("Provided pid is not valid.");

        optionalWeaponProcess.get().getProcess().destroy();
    }

    private void cleanDeadProcesses() {
        processesMap = processesMap.entrySet().stream()
                .filter(p -> p.getValue().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Optional<WeaponProcess> retrieve(Long pid) {
        cleanDeadProcesses();

        return Optional.ofNullable(processesMap.get(pid));
    }
}
