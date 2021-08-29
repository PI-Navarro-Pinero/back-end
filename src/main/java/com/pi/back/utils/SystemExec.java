package com.pi.back.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class SystemExec {

    private static final String BASE_DIR = System.getenv("BASE_DIR");
    private static final String OUTPUTS_DIR = System.getenv("OUTPUTS_DIR") + "/";

    public int executeCommand(String[] cmd, String weaponId, String actionId) {
        
        final String OUTPUT = "/" + OUTPUTS_DIR + weaponId;

        // File error = folder.newFile("./.logs/test.log");
        ProcessBuilder procBuilder = new ProcessBuilder(cmd);

        try {

            File action = new File(System.getProperty("user.dir") + "/" + BASE_DIR + OUTPUT + "/" + actionId);
            action.getParentFile().mkdirs();
            procBuilder.redirectOutput(action);

            Process proc = procBuilder.start();

//            int exitValue = proc.waitFor();
            log.info("Command '{}' executed", arrayToString(cmd));
            return 0;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private String arrayToString(String[] str) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String s : str) {
            if( !first )
                builder.append(' ');

            builder.append(s);
            first = false;
        }

        return builder.toString();
    }
}
