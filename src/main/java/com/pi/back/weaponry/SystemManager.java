package com.pi.back.weaponry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Slf4j
@Component
public class SystemManager {

    public Process execute(String command, File outputFile) throws IOException {
        List<String> splitCommand = List.of(command.split(" "));
        ProcessBuilder procBuilder = new ProcessBuilder(splitCommand);
        procBuilder.redirectOutput(outputFile);
        procBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        return start(procBuilder);
    }

    public BufferedReader execute(String command) throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder(List.of(command.split(" ")));
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
            log.error("Error executing command '{}'", command);
            log.debug("Exception '{}' thrown when executing command '{}': '{}'", e.getClass(), command, e.getCause());
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
            String errMsg = "Output file couldn't be renamed";
            log.error(errMsg);
            throw new IOException(errMsg);
        }

        return newFile;
    }

    public File findFile(String parentFilename, String fileName) {
        File file = new File(parentFilename);

        File[] filesList = file.listFiles((dir1, name) -> name.startsWith(fileName));

        if (filesList != null && filesList.length > 0) {
            return Arrays.stream(filesList)
                    .findFirst()
                    .get();
        }

        return null;
    }

    public void writeFile(String filePath, String content) throws IOException {
        Path p = Paths.get(filePath);

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, TRUNCATE_EXISTING))) {
            out.write(content.getBytes(), 0, content.length());
        }
    }
}
