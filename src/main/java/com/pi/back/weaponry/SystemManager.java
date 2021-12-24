package com.pi.back.weaponry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Slf4j
@Component
public class SystemManager {

    private final ProcessesManager processesManager;

    public SystemManager(ProcessesManager processesManager) {
        this.processesManager = processesManager;
    }

    public Process execute(String command, File outputFile) throws IOException {
        String[] commands = {"/bin/bash", "-c", command};
        ProcessBuilder procBuilder = new ProcessBuilder(commands);
        procBuilder.redirectOutput(outputFile);
        procBuilder.redirectError(outputFile);
        procBuilder.directory(outputFile.getParentFile());

        return start(procBuilder);
    }

    public BufferedReader execute(String command) throws IOException {
        String[] commands = {"/bin/bash", "-c", command};
        ProcessBuilder procBuilder = new ProcessBuilder(commands);
        Process proc = start(procBuilder);

        return new BufferedReader(new InputStreamReader(proc.getInputStream()));
    }

    private Process start(ProcessBuilder processBuilder) throws IOException {
        List<String> command = processBuilder.command();

        try {
            Process process = processBuilder.start();
            log.info("Command '{}' executed successfully", command);
            return process;
        } catch (Exception e) {
            log.error("Error while executing command '{}': {}", command, e);
            throw e;
        }
    }

    public File createDirectory(String path) {
        File file = new File(path);
        boolean directoryIsCreated;

        try {
            directoryIsCreated = file.getParentFile().mkdirs();
        } catch (Exception e) {
            log.error("Error creating output path file to '{}'", path);
            throw e;
        }

        if (directoryIsCreated)
            log.info("Directory '{}' has been created", path);

        return file;
    }

    public File renameFile(File fileToRename, String newName) throws IOException {
        String name = fileToRename.getParent() + "/" + newName;
        File newFile = new File(name);
        boolean renameSucceeded = fileToRename.renameTo(newFile);

        if (!renameSucceeded) {
            String errMsg = " Output file couldn't be renamed from '"
                    + fileToRename.getAbsolutePath() + "' to '"
                    + name + "' ";
            log.error(errMsg);
            throw new IOException(errMsg);
        }
        log.info("File {} has been renamed to {}", fileToRename.getAbsolutePath(), name);
        return newFile;
    }

    public void writeFile(String filePath, String content) throws IOException {
        Path p = Paths.get(filePath);

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))) {
            out.write(content.getBytes(), 0, content.length());
        }
    }

    public String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(Collectors.joining("\n"));
        }
    }

    public void checksumResults(Process process) throws IOException {
        File processFile = processesManager.getAllTerminatedProcesses()
                .get(process.pid())
                .getOutputFile();

        String pathToParentFile = processFile.getParentFile().getAbsolutePath();

        String command = String.format("find %s -type f ! -name \"SHA256-Sum.txt\" -exec sha256sum {} + > %s/SHA256-Sum.txt",
                pathToParentFile,
                pathToParentFile);

        execute(command);
    }
}
