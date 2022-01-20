package com.pi.back.weaponry;

import com.pi.back.weaponry.dto.WeaponProcessDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeaponProcessTest {

    @Nested
    class WeaponProcessCreationTestCase {

        Weapon weapon = Weapon.builder().build();
        Process process = Mockito.mock(Process.class);
        File file = Mockito.mock(File.class);

        ProcessHandle.Info mockInfo = Mockito.mock(ProcessHandle.Info.class);

        WeaponProcessDTO weaponProcessDTO = WeaponProcessDTO.builder()
                .weapon(weapon)
                .process(process)
                .outputFile(file)
                .build();

        @Test
        void objectIsCreatedWithCreationTimeAndCommandLine() {
            when(mockInfo.startInstant()).thenReturn(Optional.of(Instant.ofEpochSecond(100)));
            when(mockInfo.commandLine()).thenReturn(Optional.of("foo"));
            when(process.info()).thenReturn(mockInfo);

            WeaponProcess weaponProcess = new WeaponProcess(weaponProcessDTO);

            assertThat(weaponProcess.getCreationTime()).isEqualTo(Instant.ofEpochSecond(100).toString());
            assertThat(weaponProcess.getCommandLine()).isEqualTo("foo");
        }

        @Test
        void objectIsCreatedWithoutCreationTimeAndCommandLine() {
            when(mockInfo.startInstant()).thenReturn(Optional.empty());
            when(mockInfo.commandLine()).thenReturn(Optional.empty());
            when(process.info()).thenReturn(mockInfo);

            WeaponProcess weaponProcess = new WeaponProcess(weaponProcessDTO);

            assertThat(weaponProcess.getCreationTime()).isEqualTo(null);
            assertThat(weaponProcess.getCommandLine()).isEqualTo(null);
        }
    }

    @Nested
    class IsAliveTestCase {

        @Test
        void isTrueAlive() {
            Process mockProcess = Mockito.mock(Process.class);

            WeaponProcess weaponProcess = WeaponProcess.builder()
                    .process(mockProcess)
                    .build();

            when(mockProcess.isAlive()).thenReturn(true);

            assertThat(weaponProcess.isAlive()).isTrue();
        }

        @Test
        void isFalseAlive() {
            Process mockProcess = Mockito.mock(Process.class);

            WeaponProcess weaponProcess = WeaponProcess.builder()
                    .process(mockProcess)
                    .build();

            when(mockProcess.isAlive()).thenReturn(false);

            assertThat(weaponProcess.isAlive()).isFalse();
        }

    }

    @Test
    void pidIsReturned() {
        Long expectedPID = 1234L;
        Process mockProcess = Mockito.mock(Process.class);

        WeaponProcess weaponProcess = WeaponProcess.builder()
                .process(mockProcess)
                .build();

        when(mockProcess.pid()).thenReturn(expectedPID);

        assertThat(weaponProcess.pid()).isEqualTo(expectedPID);
    }

    @Test
    void destroyIsCalled() {
        Process mockProcess = Mockito.mock(Process.class);

        WeaponProcess weaponProcess = WeaponProcess.builder()
                .process(mockProcess)
                .build();

        weaponProcess.terminateProcess();

        verify(mockProcess).destroy();
    }

    @Test
    void absolutePathIsReturned() {
        File mockFile = Mockito.mock(File.class);

        WeaponProcess weaponProcess = WeaponProcess.builder()
                .outputFile(mockFile)
                .build();

        String expectedString = "foo";

        when(mockFile.getAbsolutePath()).thenReturn(expectedString);

        assertThat(weaponProcess.getStdoutFileAbsolutPath()).isEqualTo(expectedString);
    }

    @Test
    void parentFileIsReturned() {
        File mockFile = Mockito.mock(File.class);

        WeaponProcess weaponProcess = WeaponProcess.builder()
                .outputFile(mockFile)
                .build();

        String expectedString = "foobar";

        when(mockFile.getParent()).thenReturn(expectedString);

        assertThat(weaponProcess.getDirectoryAbsolutPath()).isEqualTo(expectedString);
    }
}