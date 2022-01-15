package com.pi.back.weaponry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionLauncherTest {

    ActionLauncher sut;

    @BeforeEach
    void setUp() {
        sut = ActionLauncher.builder().build();
    }

    @Test
    @DisplayName("when String is provided then field 'command' is set and self object is returned")
    void setCommand() {
        String s = "foo";
        ActionLauncher actual = sut.setCommand(s);

        assertThat(actual).isInstanceOf(ActionLauncher.class);
        assertThat(actual.getCommand()).isEqualTo(s);
    }

    @Test
    @DisplayName("when Weapon is provided then field 'weapon' is set and self object is returned")
    void setWeapon() {

        var weapon = Weapon.builder().build();
        ActionLauncher actual = sut.setWeapon(weapon);

        assertThat(actual).isInstanceOf(ActionLauncher.class);
        assertThat(actual.getWeapon()).isEqualTo(weapon);
    }

    @Test
    @DisplayName("when function is provided then is applied and field 'file' is set and self object is returned")
    void defineDirectory() {
        String st = "foo";

        Function<String, File> funct = s -> new File("/tmp/" + s);

        ActionLauncher actual = sut.defineDirectory(funct, st);

        assertThat(actual).isInstanceOf(ActionLauncher.class);
        assertThat(actual.getFile()).isEqualTo(funct.apply(st));
    }

    @Test
    @DisplayName("when consumer is provided then field 'registerWeaponProcessConsumer' is set and self object is returned")
    void registerWeaponProcess() {
        Consumer<WeaponProcess> consumer = weaponProcess -> {
        };

        ActionLauncher actual = sut.registerWeaponProcess(consumer);

        assertThat(actual).isInstanceOf(ActionLauncher.class);
        assertThat(actual.getRegisterWeaponProcessConsumer()).isEqualTo(consumer);
    }

    @Test
    @DisplayName("when consumer is provided then field 'onExitBehaviorConsumer' is set and self object is returned")
    void changeOnExitBehavior() {
        Consumer<Process> consumer = process -> {
        };

        ActionLauncher actual = sut.changeOnExitBehavior(consumer);

        assertThat(actual).isInstanceOf(ActionLauncher.class);
        assertThat(actual.getOnExitBehaviorConsumer()).isEqualTo(consumer);
    }

    @Test
    @DisplayName("when BiFunction is provided then then WeaponProcess is created and returned" +
            "field 'registerWeaponProcessConsumer' is accepted " +
            "onExit behavior of process is changed")
    void execute() {
        Weapon expectedWeapon = Weapon.builder().name("foo").actions(List.of("baz")).build();
        String expectedCommandLine = "foo";
        String expectedFilePath = "/tmp/bar";

        ActionLauncher sutLauncher = sut.toBuilder().build()
                .setWeapon(expectedWeapon)
                .setCommand(expectedCommandLine)
                .defineDirectory(File::new, expectedFilePath)
                .changeOnExitBehavior(process -> {
                })
                .registerWeaponProcess(weaponProcess -> {
                });

        Process mockProcess = Mockito.mock(Process.class);
        ProcessHandle.Info mockInfo = Mockito.mock(ProcessHandle.Info.class);
        when(mockInfo.commandLine()).thenReturn(Optional.of(expectedCommandLine));
        when(mockProcess.info()).thenReturn(mockInfo);
        when(mockProcess.onExit()).thenReturn(CompletableFuture.completedFuture(mockProcess));

        WeaponProcess actual = sutLauncher.execute((s, file) -> mockProcess);

        assertThat(actual).isInstanceOf(WeaponProcess.class);
        assertThat(actual.getWeapon()).isEqualTo(expectedWeapon);
        assertThat(actual.getCommandLine()).isEqualTo(expectedCommandLine);
        assertThat(actual.getProcess()).isEqualTo(mockProcess);
        assertThat(actual.getOutputFile().getAbsolutePath()).isEqualTo(expectedFilePath);
        verify(mockProcess).onExit();
    }
}