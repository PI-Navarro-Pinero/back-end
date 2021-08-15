package com.pi.back.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class SystemExecTest {

    SystemExec executor;

    @BeforeEach
    void setUp() {
        executor = new SystemExec();
    }

    @Test
    @DisplayName("Execute 'ls' command")
    void executeCommandTest() {
        String[] cmd = {"ls", "-la"};
        String weaponId = "0";
        String actionId = "0";

        executor.executeCommand(cmd, weaponId, actionId);
        File file = new File("/home/mrgreen/pinp/outputs/0/0");
        if(file.exists() && file.length() != 0)
            assert(true);
        else
            assert(false);
    }
}
