package com.pi.back.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        int exitCode = executor.executeCommand(cmd, weaponId, actionId);

        assert(exitCode != -1);
    }
}
