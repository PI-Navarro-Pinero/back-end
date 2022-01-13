package com.pi.back.services;

import com.pi.back.utils.CommandValidator;
import com.pi.back.weaponry.ProcessesManager;
import com.pi.back.weaponry.SystemManager;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import com.pi.back.weaponry.Weaponry;
import com.pi.back.weaponry.WeaponsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
    class RunActionTestCase {
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
    class RunCommandTestCase {

//        @Test
//        @DisplayName("when systemManager#execute returns BufferedReader then return Stream of String")
//        void bufferedReaderReturned() throws IOException {
//            Stream<String> expectedList = Stream.of("foo", "bar", "foobar");
//
//            BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
//            Mockito.when(bufferedReader.lines()).thenReturn(expectedList);
//            when(systemManager.execute(any())).thenReturn(bufferedReader);
//
//            Stream<String> actual = sut.runCommand("waldo");
//
//            assertThat(actual).isEqualTo(expectedList);
//        }

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
    class GetProcessPathnameTestCase {

        @Test
        @DisplayName("when processesManager#getProcessPath throws Exception then re-throw")
        void exceptionThrown() throws InvalidAttributesException {
            String expectedErrMsg = "foo";

            when(processesManager.getProcessStdoutFilePath(any())).thenThrow(new InvalidAttributesException(expectedErrMsg));

            assertThatThrownBy(() -> sut.getProcessPathname(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessageContaining(expectedErrMsg);
        }

        @Test
        @DisplayName("when processesManager#getProcessPath return String then return it")
        void stringIsReturned() throws InvalidAttributesException {
            String expected = "foobar";

            when(processesManager.getProcessStdoutFilePath(any())).thenReturn(expected);

            String actual = sut.getProcessPathname(123L);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class GetProcessDirectoryPathnameTestCase {

        @Test
        @DisplayName("when processesManager#getProcessDirectoryPath throws Exception then re-throw")
        void exceptionThrown() throws InvalidAttributesException {
            String expectedErrMsg = "foo";

            when(processesManager.getProcessDirectoryPath(any())).thenThrow(new InvalidAttributesException(expectedErrMsg));

            assertThatThrownBy(() -> sut.getProcessDirectoryPathname(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessageContaining(expectedErrMsg);
        }

        @Test
        @DisplayName("processesManager#getProcessDirectoryPath return String then return it")
        void stringIsReturned() throws InvalidAttributesException {
            String expected = "foobar";

            when(processesManager.getProcessDirectoryPath(any())).thenReturn(expected);

            String actual = sut.getProcessDirectoryPathname(123L);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class InputToProcessTestCase {

        @Test
        @DisplayName("when called then processManager#writeIntoProcess is invoked")
        void writeIntoProcessIsInvoked() throws InvalidAttributesException {
            sut.inputToProcess(1L, "foo");

            verify(processesManager).writeIntoProcess(1L, "foo");
        }

        @Test
        @DisplayName("when processesManager#getRunningProcess throws InvalidAttributesException then re-throw")
        void invalidAttributesExceptionIsThrown() throws InvalidAttributesException {
            doThrow(InvalidAttributesException.class).when(processesManager).writeIntoProcess(any(), any());

            assertThatThrownBy(() -> sut.inputToProcess(1L, "foo"))
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }

        @Test
        @DisplayName("when processesManager#getRunningProcess throws Exception then re-throw")
        void exceptionIsThrown() throws InvalidAttributesException {
            doThrow(RuntimeException.class).when(processesManager).writeIntoProcess(any(), any());

            assertThatThrownBy(() -> sut.inputToProcess(1L, "foo"))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    class GetAvailableWeaponsTestCase {
        @DisplayName("when weaponsRepository#getWeaponsList returns Weaponry then return List of Weapon")
        @Test
        void filledList() {
            List<Weapon> expectedWeapon = List.of(Weapon.builder()
                    .build());
            Weaponry expectedWeaponry = Weaponry.builder()
                    .weaponry(expectedWeapon)
                    .build();

            when(weaponsRepository.getWeaponsList()).thenReturn(expectedWeaponry);

            List<Weapon> actual = sut.getAvailableWeapons();

            assertThat(actual).isEqualTo(expectedWeapon);
        }

        @Test
        @DisplayName("when weaponsRepository#getWeaponsList returns null then return empty list")
        void emptyList() {
            when(weaponsRepository.getWeaponsList()).thenReturn(null);

            List<Weapon> actual = sut.getAvailableWeapons();

            assertThat(actual).isEqualTo(List.of());
        }
    }

    @Nested
    class GetWeaponTestCase {

        @Test
        @DisplayName("when weaponsRepository#findWeapon return Optional with present value then return Weapon")
        void presentValue() throws InvalidAttributesException {
            Weapon expectedWeapon = Weapon.builder()
                    .build();

            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.of(expectedWeapon));

            Weapon actual = sut.getWeapon(1);

            assertThat(actual).isEqualTo(expectedWeapon);
        }

        @Test
        @DisplayName("when weaponsRepository#findWeapon return Optional with absent value then throw InvalidAttributesException")
        void absentValue() {
            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.getWeapon(1))
                    .hasMessageContaining("Requested weapon with id 1 does not exists.")
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }
    }

    @Nested
    class GetConfigurationFilePathTestCase {

        @Test
        @DisplayName("when weaponsRepository#findWeapon return Optional with Weapon having configuration file then return absolute path")
        void absolutePathIsReturned() throws InvalidAttributesException {
            File expectedFile = new File("/tmp/foo");


            Weapon expectedWeapon = Weapon.builder()
                    .configFile(expectedFile)
                    .build();

            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.of(expectedWeapon));

            String actual = sut.getConfigurationFilePath(1);

            assertThat(actual).isEqualTo("/tmp/foo");
        }

        @Test
        @DisplayName("when weaponsRepository#findWeapon return Optional with Weapon not having configuration file then throw InvalidAttributesException")
        void noConfigurationFile() {
            Weapon expectedWeapon = Weapon.builder()
                    .name("foo")
                    .build();

            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.of(expectedWeapon));

            assertThatThrownBy(() -> sut.getConfigurationFilePath(1))
                    .hasMessageContaining("Requested weapon 'foo' does not require a configuration file")
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }

        @Test
        @DisplayName("when weaponsRepository#findWeapon return Optional with absent value then throw InvalidAttributesException")
        void exceptionThrownByRepository() {
            when(weaponsRepository.findWeapon(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.getConfigurationFilePath(1))
                    .hasMessageContaining("Requested weapon with id 1 does not exists.")
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }
    }

    @Nested
    class GetRunningActionsTestCase {

        @Test
        @DisplayName("when processesManager#getAllRunningProcesses returns Map<Long, WeaponProcess> then return it")
        void mapReturned() {
            var mockWeaponProcess = WeaponProcess.builder()
                    .build();
            Map<Long, WeaponProcess> expectedMap = new HashMap<>();
            expectedMap.put(1L, mockWeaponProcess);

            when(processesManager.getAllRunningProcesses()).thenReturn(expectedMap);

            Map<Long, WeaponProcess> actual = sut.getRunningActions();

            assertThat(actual).isEqualTo(expectedMap);
        }
    }

    @Nested
    class GetFinalizedActionsTestCase {

        @Test
        @DisplayName("when processesManager#getAllTerminatedProcesses returns Map<Long, WeaponProcess> then return it")
        void mapReturned() {
            var mockWeaponProcess = WeaponProcess.builder()
                    .build();
            Map<Long, WeaponProcess> expectedMap = new HashMap<>();
            expectedMap.put(1L, mockWeaponProcess);

            when(processesManager.getAllTerminatedProcesses()).thenReturn(expectedMap);

            Map<Long, WeaponProcess> actual = sut.getFinalizedActions();

            assertThat(actual).isEqualTo(expectedMap);
        }
    }

    @Nested
    class KillRunningActionTestCase {

        @Test
        @DisplayName("when processesManager#terminate throws InvalidAttributesException then return it")
        void exceptionThrown() throws InvalidAttributesException {
            doThrow(InvalidAttributesException.class).when(processesManager).terminate(any());

            assertThatThrownBy(() -> sut.killRunningAction(1L))
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }

        @Test
        @DisplayName("when called then processManager#terminate is invoked")
        void terminateInvoked() throws InvalidAttributesException {
            sut.killRunningAction(1L);

            verify(processesManager).terminate(1L);
        }
    }

    @Nested
    class WriteFileTestCase {
        @Test
        @DisplayName("when called then systemManager#writeFile is invoked")
        void writeFileInvoked() throws IOException {
            sut.writeFile("foo", "bar");

            verify(systemManager).writeFile("foo", "bar");
        }

        @Test
        @DisplayName("when systemManager#writeFile throws IOException then return it")
        void ioExceptionThrown() throws IOException {
            doThrow(IOException.class).when(systemManager).writeFile(any(), any());

            assertThatThrownBy(() -> sut.writeFile("foo", "bar"))
                    .isExactlyInstanceOf(IOException.class);
        }
    }

    @Nested
    class ReadFileCaseTest {

        @Test
        @DisplayName("when systemManager#readFile returns String then return it")
        void stringReturned() throws IOException, InvalidAttributesException {
            String expectedMessage = "foo";

            when(systemManager.readFile(any())).thenReturn(expectedMessage);

            String actual = sut.readFile("foobar");

            assertThat(actual).isEqualTo(expectedMessage);
        }

        @Test
        @DisplayName("when systemManager#readFile throws IOException then throw InvalidAttributesException")
        void ioExceptionThrown() throws IOException {
            when(systemManager.readFile(any())).thenThrow(IOException.class);

            assertThatThrownBy(() -> sut.readFile("foobar"))
                    .hasMessageContaining("Invalid file name")
                    .isExactlyInstanceOf(InvalidAttributesException.class);
        }

        @Test
        @DisplayName("when systemManager#readFile throws InvalidPathException then throw InvalidPathException")
        void invalidPathExceptionThrown() throws IOException {
            when(systemManager.readFile(any())).thenThrow(InvalidPathException.class);

            assertThatThrownBy(() -> sut.readFile("foobar"))
                    .isExactlyInstanceOf(InvalidPathException.class);
        }
    }
}