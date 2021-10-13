package com.pi.back.weaponry;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.time.Instant;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class WeaponProcess {

    private final Process process;
    private final Weapon weapon;
    private final File outputFile;

    public boolean isAlive() {
        return process.isAlive();
    }

    public Long pid() {
        return process.pid();
    }

    public String creationTime() {
        return process.info()
                .startInstant()
                .map(Instant::toString)
                .orElse("unknown");
    }

    public String commandLine() {
        return process.info().commandLine().orElse(null);
    }

    public void terminateProcess() {
        process.destroy();
    }

    public String getPathname() {
        return outputFile.getAbsolutePath();
    }

    public static WeaponProcess newInstance(Process p, Weapon w, File f) {
        return WeaponProcess.builder()
                .process(p)
                .weapon(w)
                .outputFile(f)
                .build();
    }
}
