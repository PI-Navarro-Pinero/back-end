package com.pi.back.weaponry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.naming.directory.InvalidAttributesException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessesManagerTest {

    ProcessesManager sut;

    @BeforeEach
    void setUp() {
        sut = new ProcessesManager();
    }

    @Nested
    class InsertTestCase {

        @Test
        @DisplayName("when insert invoked with WeaponProcess then map should be updated")
        void insertNotNull() {
            Process mockProcess = Mockito.mock(Process.class);
            when(mockProcess.pid()).thenReturn(123L);

            WeaponProcess wp = WeaponProcess.builder()
                    .process(mockProcess)
                    .build();

            assertThat(sut.getProcessesMap().size()).isEqualTo(0);

            sut.insert(wp);

            assertThat(sut.getProcessesMap().size()).isEqualTo(1);
            assertThat(sut.getProcessesMap().get(123L)).isEqualTo(wp);
        }

        @Test
        @DisplayName("when insert is invoked with null argument then map is not updated")
        void insertNull() {
            sut.insert(null);

            assertThat(sut.getProcessesMap().size()).isEqualTo(0);
        }
    }

    @Nested
    class GetAllRunningProcessesTestCase {

        @Test
        @DisplayName("when map contains alive and dead processes then return only alive ones")
        void mapContainsEntries() {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(true);

            Process aliveProcess2 = Mockito.mock(Process.class);
            when(aliveProcess2.pid()).thenReturn(321L);
            when(aliveProcess2.isAlive()).thenReturn(true);

            Process deadProcess1 = Mockito.mock(Process.class);
            when(deadProcess1.pid()).thenReturn(111L);
            when(deadProcess1.isAlive()).thenReturn(false);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            WeaponProcess wp2 = WeaponProcess.builder()
                    .process(aliveProcess2)
                    .build();

            WeaponProcess wp3 = WeaponProcess.builder()
                    .process(deadProcess1)
                    .build();

            sut.insert(wp1);
            sut.insert(wp2);
            sut.insert(wp3);

            Map<Long, WeaponProcess> actual = sut.getAllRunningProcesses();

            assertThat(actual.size()).isEqualTo(2);
            assertThat(actual.get(123L)).isEqualTo(wp1);
            assertThat(actual.get(321L)).isEqualTo(wp2);
        }

        @Test
        @DisplayName("when map contains only dead processes then return empty")
        void mapContainsOnlyDeadProcesses() {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(false);

            Process aliveProcess2 = Mockito.mock(Process.class);
            when(aliveProcess2.pid()).thenReturn(321L);
            when(aliveProcess2.isAlive()).thenReturn(false);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            WeaponProcess wp2 = WeaponProcess.builder()
                    .process(aliveProcess2)
                    .build();

            sut.insert(wp1);
            sut.insert(wp2);

            Map<Long, WeaponProcess> actual = sut.getAllRunningProcesses();

            assertThat(actual.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("when map contains no entry at all then return empty map")
        void mapIsEmpty() {
            Map<Long, WeaponProcess> actual = sut.getAllRunningProcesses();

            assertThat(actual.size()).isEqualTo(0);
        }
    }

    @Nested
    class GetAllTerminatedProcessesTestCase {

        @Test
        @DisplayName("when map contains alive and dead processes then return only dead ones")
        void mapContainsEntries() {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(true);

            Process aliveProcess2 = Mockito.mock(Process.class);
            when(aliveProcess2.pid()).thenReturn(321L);
            when(aliveProcess2.isAlive()).thenReturn(false);

            Process deadProcess1 = Mockito.mock(Process.class);
            when(deadProcess1.pid()).thenReturn(111L);
            when(deadProcess1.isAlive()).thenReturn(false);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            WeaponProcess wp2 = WeaponProcess.builder()
                    .process(aliveProcess2)
                    .build();

            WeaponProcess wp3 = WeaponProcess.builder()
                    .process(deadProcess1)
                    .build();

            sut.insert(wp1);
            sut.insert(wp2);
            sut.insert(wp3);

            Map<Long, WeaponProcess> actual = sut.getAllTerminatedProcesses();

            assertThat(actual.size()).isEqualTo(2);
            assertThat(actual.get(321L)).isEqualTo(wp2);
            assertThat(actual.get(111L)).isEqualTo(wp3);
        }

        @Test
        @DisplayName("when map contains only alive processes then return empty")
        void mapContainsOnlyAliveProcesses() {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(true);

            Process aliveProcess2 = Mockito.mock(Process.class);
            when(aliveProcess2.pid()).thenReturn(321L);
            when(aliveProcess2.isAlive()).thenReturn(true);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            WeaponProcess wp2 = WeaponProcess.builder()
                    .process(aliveProcess2)
                    .build();

            sut.insert(wp1);
            sut.insert(wp2);

            Map<Long, WeaponProcess> actual = sut.getAllTerminatedProcesses();

            assertThat(actual.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("when map contains no entry at all then return empty map")
        void mapIsEmpty() {
            Map<Long, WeaponProcess> actual = sut.getAllTerminatedProcesses();

            assertThat(actual.size()).isEqualTo(0);
        }
    }

    @Nested
    class GetRunningProcessesTestCase {

        @Test
        @DisplayName("when pid of alive process is provided then return it's Process object")
        void pidOfAliveProcess() throws InvalidAttributesException {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(true);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            sut.insert(wp1);

            Process actual = sut.getRunningProcess(123L);

            assertThat(actual).isEqualTo(aliveProcess1);
        }

        @Test
        @DisplayName("when pid of dead process is provided then throw IAE")
        void pidOfDeadProcess() {
            Process aliveProcess1 = Mockito.mock(Process.class);
            when(aliveProcess1.pid()).thenReturn(123L);
            when(aliveProcess1.isAlive()).thenReturn(false);

            WeaponProcess wp1 = WeaponProcess.builder()
                    .process(aliveProcess1)
                    .build();

            sut.insert(wp1);

            assertThatThrownBy(() -> sut.getRunningProcess(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessage("Provided pid 123 does not belong to any running process.");
        }
    }

    @Nested
    class GetProcessStdoutFilePathTestCase {
        @Test
        @DisplayName("when pid of existing process is provided then return String path")
        void validPid() throws InvalidAttributesException {
            WeaponProcess wp = Mockito.mock(WeaponProcess.class);
            when(wp.getStdoutFileAbsolutPath()).thenReturn("/foo/bar");
            when(wp.pid()).thenReturn(123L);

            sut.insert(wp);

            String actual = sut.getProcessStdoutFilePath(123L);

            assertThat(actual).isEqualTo("/foo/bar");
        }

        @Test
        @DisplayName("when pid of not existing process is provided then throw IAE")
        void invalidPid() {
            assertThatThrownBy(() -> sut.getProcessStdoutFilePath(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessage("Provided pid 123 does not belong to any process.");
        }
    }

    @Nested
    class GetProcessDirectoryPathTestCase {

        @Test
        @DisplayName("when valid pid provided then return string")
        void validPid() throws InvalidAttributesException {
            WeaponProcess wp = Mockito.mock(WeaponProcess.class);
            when(wp.getDirectoryAbsolutPath()).thenReturn("/foo/bar");
            when(wp.pid()).thenReturn(123L);

            sut.insert(wp);

            String actual = sut.getProcessDirectoryPath(123L);

            assertThat(actual).isEqualTo("/foo/bar");
        }

        @Test
        @DisplayName("when pid of not existing process is provided then throw IAE")
        void invalidPid() {
            assertThatThrownBy(() -> sut.getProcessDirectoryPath(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessage("Provided pid 123 does not belong to any process.");
        }
    }

    @Nested
    class TerminateTestCase {

        @Test
        @DisplayName("when provided pid belongs to running process then invoke terminateProcess method")
        void pidOfRunningProcess() throws InvalidAttributesException {
            WeaponProcess wp = Mockito.mock(WeaponProcess.class);
            when(wp.isAlive()).thenReturn(true);
            when(wp.pid()).thenReturn(123L);

            sut.insert(wp);

            sut.terminate(123L);

            verify(wp).terminateProcess();
        }

        @Test
        @DisplayName("when provided pid does not belong to any running process then throw IAE")
        void invalidPid() {
            assertThatThrownBy(() -> sut.terminate(123L))
                    .isExactlyInstanceOf(InvalidAttributesException.class)
                    .hasMessage("Provided pid 123 does not belong to any running process.");
        }
    }
}