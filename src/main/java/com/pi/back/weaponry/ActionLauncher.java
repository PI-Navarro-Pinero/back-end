package com.pi.back.weaponry;

import com.pi.back.utils.WeaponProcessDTO;
import lombok.Builder;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Builder(toBuilder = true)
public class ActionLauncher implements Action {

    private File file;
    private String command;
    private Weapon weapon;
    private Consumer<Process> onExitBehaviorConsumer;
    private Consumer<WeaponProcess> registerWeaponProcessConsumer;

    @Override
    public ActionLauncher setCommand(String command) {
        this.command = command;

        return this;
    }

    @Override
    public ActionLauncher setWeapon(Weapon w) {
        this.weapon = w;

        return this;
    }

    @Override
    public ActionLauncher defineDirectory(Function<String, File> fileCreationFunction, String path) {
        file = fileCreationFunction.apply(path);

        return this;
    }

    @Override
    public ActionLauncher registerWeaponProcess(Consumer<WeaponProcess> registerWeaponProcessOperation) {
        registerWeaponProcessConsumer = registerWeaponProcessOperation;

        return this;
    }

    @Override
    public ActionLauncher changeOnExitBehavior(Consumer<Process> onExitOperation) {
        this.onExitBehaviorConsumer = onExitOperation;

        return this;
    }

    @Override
    public WeaponProcess execute(BiFunction<String, File, Process> executionOperation) {
        Process process = executionOperation.apply(command, file);

        var weaponProcess = new WeaponProcess(WeaponProcessDTO.builder()
                .outputFile(file)
                .process(process)
                .weapon(weapon)
                .build());

        registerWeaponProcessConsumer.accept(weaponProcess);
        process.onExit().thenAccept(onExitBehaviorConsumer);

        return weaponProcess;
    }
}
