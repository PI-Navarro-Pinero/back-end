package com.pi.back.utils;

import com.pi.back.weaponry.Weapon;
import lombok.Builder;
import lombok.Getter;

import java.io.File;

@Builder
@Getter
public class WeaponProcessDTO {
    private final Process process;
    private final Weapon weapon;
    private final File outputFile;
}
