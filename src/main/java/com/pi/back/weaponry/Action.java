package com.pi.back.weaponry;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Action {

    ActionLauncher setCommand(String command);

    ActionLauncher setWeapon(Weapon w);

    ActionLauncher defineDirectory(Function<String, File> fileFunction, String s);

    ActionLauncher registerWeaponProcess(Consumer<WeaponProcess> consumer);

    ActionLauncher changeOnExitBehavior(Consumer<Process> processConsumer);

    WeaponProcess execute(BiFunction<String, File, Process> executeBiFunction);
}
