package com.pi.back.weaponry;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.naming.directory.InvalidAttributesException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProcessesManager {

    @Getter(value = AccessLevel.PROTECTED)
    private Map<Long, WeaponProcess> processesMap;

    public ProcessesManager() {
        this.processesMap = new HashMap<>();
    }

    public void insert(WeaponProcess newProcess) {
        if (newProcess != null)
            processesMap.put(newProcess.pid(), newProcess);
    }

    public Map<Long, WeaponProcess> getAllRunningProcesses() {
        return processesMap.entrySet().stream()
                .filter(p -> p.getValue().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Long, WeaponProcess> getAllTerminatedProcesses() {
        return processesMap.entrySet().stream()
                .filter(p -> !p.getValue().isAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Process getRunningProcess(Long pid) throws InvalidAttributesException {
        try {
            return getAllRunningProcesses().get(pid).getProcess();
        } catch (Exception e) {
            throw new InvalidAttributesException("Provided pid " + pid + " does not belong to any running process.");
        }
    }

    public String getProcessStdoutFilePath(Long pid) throws InvalidAttributesException {
        try {
            return processesMap.get(pid).getStdoutFileAbsolutPath();
        } catch (Exception e) {
            throw new InvalidAttributesException("Provided pid " + pid + " does not belong to any process.");
        }
    }

    public String getProcessDirectoryPath(Long pid) throws InvalidAttributesException {
        try {
            return processesMap.get(pid).getDirectoryAbsolutPath();
        } catch (Exception e) {
            throw new InvalidAttributesException("Provided pid " + pid + " does not belong to any process.");
        }
    }

    public void terminate(Long pid) throws InvalidAttributesException {
        try {
            getAllRunningProcesses().get(pid).terminateProcess();
        } catch (Exception e) {
            throw new InvalidAttributesException("Provided pid " + pid + " does not belong to any running process.");
        }
    }

    public void writeIntoProcess(Long pid, String input) throws InvalidAttributesException {
        OutputStream outputStream = getRunningProcess(pid).getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println(input);
        printWriter.flush();
    }
}
