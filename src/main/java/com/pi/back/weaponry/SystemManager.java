package com.pi.back.weaponry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Slf4j
@Component
public class SystemManager {

    private final ProcessesManager processesManager;

    @Autowired
    public SystemManager(ProcessesManager processesManager) {
        this.processesManager = processesManager;
    }

    public Process execute(String command, File outputFile) throws IOException {
        String[] commands = {"/bin/bash", "-c", command};
        ProcessBuilder procBuilder = new ProcessBuilder(commands);

        return procBuilder
                .redirectOutput(outputFile)
                .redirectError(outputFile)
                .directory(outputFile.getParentFile())
                .start();
    }

    public Process execute(String command) throws IOException {
        String[] commands = {"/bin/bash", "-c", command};
        ProcessBuilder procBuilder = new ProcessBuilder(commands);

        return procBuilder.start();
    }

    public File createDirectory(String path) {
        File file = new File(path);
        boolean directoryIsCreated;

        directoryIsCreated = file.getParentFile().mkdirs();

        if (directoryIsCreated)
            log.info("Directory '{}' has been created", path);

        return file;
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
