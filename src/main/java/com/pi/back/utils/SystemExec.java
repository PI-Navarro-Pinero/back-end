package com.pi.back.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class SystemExec {
    
    static final String HOME_PATH = "/home/mrgreen/pinp/";

    public void executeCommand(String cmd[], String weaponId, String actionId) {
        
        final String OUTPUT_PATH = HOME_PATH + "outputs/" + weaponId + "/" + actionId;

        // File error = folder.newFile("./.logs/test.log");

        ProcessBuilder procBuilder = new ProcessBuilder(cmd);
        procBuilder.directory(new File(HOME_PATH));
        
        procBuilder.redirectOutput(new File (OUTPUT_PATH));

        try {
            Process proc = procBuilder.start();

            int exitValue = proc.waitFor();
            log.info("Exit code {}", exitValue);
            log.info("Command: {}", arrayToString(cmd));
        } catch (InterruptedException | IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private String arrayToString(String str[]) {
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
