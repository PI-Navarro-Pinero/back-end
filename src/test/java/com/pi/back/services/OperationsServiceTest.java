package com.pi.back.services;

import com.pi.back.weaponry.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationsServiceTest {
    @Mock
    private CommandValidator commandValidator;
    @Mock
    private ProcessesManager processesManager;
    @Mock
    private WeaponsRepository weaponsRepository;
    @Mock
    private SystemManager systemManager;
    @InjectMocks
    private OperationsService sut;

    @Nested
    class runAction {
       /* @Test
        @DisplayName("when parameters are valid then return WeaponProcess")
        void validParameters() throws InvalidAttributesException, IOException {
            var mockWeapon = buildWeapon("foo", "bar", List.of("foobar"), new File("baz"));
            var mockFile = new File("foo");

            ProcessBuilder process = new ProcessBuilder("foo");
            Process mockProcess = process.start();

            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.of(mockWeapon));
            when(commandValidator.buildCommand(any(), any())).thenReturn("quz");
            when(systemManager.createDirectory(any())).thenReturn(mockFile);
            when(systemManager.execute(any(), any())).thenReturn(mockProcess);
            when(systemManager.renameFile(any(), any())).thenReturn(mockFile);
            WeaponProcess expectedWeaponProcess = new WeaponProcess(mockProcess, mockWeapon, mockFile);

            WeaponProcess actualWeaponProcess = sut.runAction(1, 2, List.of(""));

            assertThat(actualWeaponProcess).isEqualTo(expectedWeaponProcess);
        }*/
        // when weaponId is not valid then throw InvalidAttributesException
        // when actionId is not valid then throw InvalidAttributesException
        // when queryParamList is not valid then throw InvalidAttributesException
        // when commandValidator throws Exception then re-throw
        // when systemManager#createDirectory throws Exception then throw IOException
        // when systemManager#execute throws Exception then throw IOException
        // when systemManager#renameFile throws Exception then throw IOException
    }

    @Nested
    class RunCommandTest {

        @Test
        @DisplayName("when systemManager#execute returns BufferedReader then return Stream of String")
        void bufferedReaderReturned() throws IOException {
            Stream<String> expectedList = Stream.of("foo", "bar", "foobar");

            BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
            Mockito.when(bufferedReader.lines()).thenReturn(expectedList);
            when(systemManager.execute(any())).thenReturn(bufferedReader);

            Stream<String> actual = sut.runCommand("waldo");

            assertThat(actual).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("when systemManager#execute throws Exception then throw IOException")
        void exceptionThrown() throws IOException {
            when(systemManager.execute(any())).thenThrow(RuntimeException.class);

            assertThatThrownBy(() -> sut.runCommand("waldo"))
                    .hasMessageContaining("Command 'waldo' execution failed.")
                    .isExactlyInstanceOf(IOException.class);
        }
    }

    @Nested
    class getProcessPathname {
        // when processesManager#getProcessPath throws Exception then re-throw
        // when processesManager#getProcessPath return String then return it
    }

    @Nested
    class getProcessDirectoryPathname {
        // when processesManager#getProcessDirectoryPath throws Exception then re-throw
        // when processesManager#getProcessDirectoryPath return String then return it
    }

    @Nested
    class inputToProcess {
        // when processesManager#getRunningProcess returns OutputStream then create PrintWriter, print and flush
        // when processesManager#getRunningProcess throws InvalidAttributesException then re-throw
    }

    @Nested
    class getAvailableWeapons {
        // when weaponsRepository#getWeaponsList returns Weaponry then return List of Weapon
        // when weaponsRepository#getWeaponsList returns null then throw NullPointerException
    }

    @Nested
    class getWeapon {
        // when weaponsRepository#findWeapon return Optional with present value then return Weapon
        // when weaponsRepository#findWeapon return Optional with absent value then throw InvalidAttributesException
    }

    @Nested
    class getConfigurationFilePath {
        // when weaponsRepository#findWeapon return Optional with Weapon having configuration file then return absolute path
        // when weaponsRepository#findWeapon return Optional with Weapon not having configuration file then throw InvalidAttributesException
        // when weaponsRepository#findWeapon return Optional with absent value then throw InvalidAttributesException
    }

    @Nested
    class getRunningActions {
        // when processesManager#getAllRunningProcesses returns Map<Long, WeaponProcess> then return it
    }

    @Nested
    class getFinalizedActions {
        // when processesManager#getAllTerminatedProcesses returns Map<Long, WeaponProcess> then return it
    }

    @Nested
    class killRunningAction {
        // when processesManager#terminate throws InvalidAttributesException then return it
    }

    @Nested
    class writeFile {
        // when systemManager#writeFile throws IOException then return it
    }

    @Nested
    class readFile {
        // when systemManager#readFile returns String then return it
        // when systemManager#readFile throws IOException then throw InvalidAttributesException
        // when systemManager#readFile throws InvalidPathException then throw InvalidPathException
    }
}