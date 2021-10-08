package com.pi.back.weaponry;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class WeaponProcess {

    private Process process;
    private Weapon weapon;

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

    public String command() {
        return process.info().command().orElse(null);
    }

    public static WeaponProcess newInstance(Process p, Weapon w) {
        return WeaponProcess.builder()
                .process(p)
                .weapon(w)
                .build();
    }
}
