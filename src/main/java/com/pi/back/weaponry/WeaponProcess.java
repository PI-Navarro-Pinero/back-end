package com.pi.back.weaponry;

import com.pi.back.utils.WeaponProcessDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class WeaponProcess {

    private final Process process;
    private final Weapon weapon;
    private final File outputFile;
    private final String creationTime;
    private final String commandLine;

    public WeaponProcess(WeaponProcessDTO dto) {
        this.process = dto.getProcess();
        this.weapon = dto.getWeapon();
        this.outputFile = dto.getOutputFile();
        this.creationTime = creationTime();
        this.commandLine = commandLine();
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    public Long pid() {
        return process.pid();
    }

    public void terminateProcess() {
        process.destroy();
    }

    public String getStdoutFileAbsolutPath() {
        return outputFile.getAbsolutePath();
    }

    public String getDirectoryAbsolutPath() {
        return outputFile.getParent();
    }

    private String creationTime() {
        return process.info()
                .startInstant()
                .map(Instant::toString)
                .orElse(null);
    }

    private String commandLine() {
        return process.info().commandLine().orElse(null);
    }
}
