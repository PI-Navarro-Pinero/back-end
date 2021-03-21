package com.pi.back.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SystemExec {
    public void executeCommand(String cmd) {
        try {
            Process p=Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=reader.readLine();
            while(line!=null) {
                line=reader.readLine();
            }
        }
        catch(IOException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
