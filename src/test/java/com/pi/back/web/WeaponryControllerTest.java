package com.pi.back.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.back.services.OperationsService;
import com.pi.back.utils.WeaponProcessDTO;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.naming.directory.InvalidAttributesException;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WeaponryControllerTest {

    private static final String WEAPONRY_PATH = "/weaponry";
    private static final String FETCH_WEAPON_PATH = "/weaponry/1";
    private static final String CONFIGURATION_FILE_PATH = "/weaponry/1/configuration-file";
    private static final String EXECUTE_ACTION_PATH = "/weaponry/1/actions/1";
    private static final String LAUNCHED_ACTIONS_PATH = "/launched-actions";
    private static final String LAUNCHED_ACTIVE_ACTIONS_PATH = "/launched-actions/active";
    private static final String STDOUT_PATH = "/launched-actions/1/stdout";
    private static final String ACTION_OUTPUT_PATH = "/launched-actions/1/files";
    private static final String READ_ACTION_OUTPUT_PATH = "/launched-actions/1/files/foo";
    private static final String KILL_ACTION_PATH = "/launched-actions/active/1/terminate";
    private static final String WRITE_ACTION_PATH = "/launched-actions/active/1/stdin";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private OperationsService operationsService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new WeaponryController(operationsService))
                .build();
    }

    @Nested
    class FetchWeaponryTestCase {

        @Test
        @DisplayName("when at least one weapon is available then return 200 OK")
        void weaponryIsNotEmpty() throws Exception {
            var mockWeapon = Weapon.builder()
                    .name("foo")
                    .actions(List.of("bar", "baz"))
                    .configFile(File.createTempFile("foo", null))
                    .description("foobar")
                    .build();

            when(operationsService.getAvailableWeapons()).thenReturn(List.of(mockWeapon));

            mockMvc.perform(get(WEAPONRY_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weaponry", hasSize(1)))
                    .andExpect(jsonPath("$.weaponry.[0].id", is(0)))
                    .andExpect(jsonPath("$.weaponry.[0].name", is(mockWeapon.getName())))
                    .andExpect(jsonPath("$.weaponry.[0].configurationFile", is(true)))
                    .andExpect(jsonPath("$.weaponry.[0].description", is(mockWeapon.getDescription())))
                    .andExpect(jsonPath("$.weaponry.[0].actions.['0']", is(mockWeapon.getActions().get(0))))
                    .andExpect(jsonPath("$.weaponry.[0].actions.['1']", is(mockWeapon.getActions().get(1))));
        }

        @Test
        @DisplayName("when no weapon is available then return 204 No Content")
        void weaponryIsEmpty() throws Exception {
            when(operationsService.getAvailableWeapons()).thenReturn(List.of());

            mockMvc.perform(get(WEAPONRY_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class FetchWeaponActionsTestCase {

        @Test
        @DisplayName("When weapon id is valid then return 200 OK")
        void weaponIdValid() throws Exception {
            var mockWeapon = Weapon.builder()
                    .name("foo")
                    .actions(List.of("bar", "baz"))
                    .configFile(File.createTempFile("foo", null))
                    .description("foobar")
                    .build();

            when(operationsService.getWeapon(any())).thenReturn(mockWeapon);

            mockMvc.perform(get(FETCH_WEAPON_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is(mockWeapon.getName())))
                    .andExpect(jsonPath("$.configurationFile", is(true)))
                    .andExpect(jsonPath("$.description", is(mockWeapon.getDescription())))
                    .andExpect(jsonPath("$.actions.['0']", is(mockWeapon.getActions().get(0))));
        }

        @Test
        @DisplayName("When weapon id is not valid then return 412 Precondition Failed")
        void weaponIdIsInvalid() throws Exception {
            String errMessage = "foo";

            InvalidAttributesException exception = new InvalidAttributesException(errMessage);

            when(operationsService.getWeapon(any())).thenThrow(exception);

            mockMvc.perform(get(FETCH_WEAPON_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(errMessage)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getWeapon(any())).thenThrow(RuntimeException.class);

            mockMvc.perform(get(FETCH_WEAPON_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetConfigurationFileTestCase {

        @Test
        @DisplayName("When configuration file is required then return 200 OK")
        void configurationFileIsRequired() throws Exception {
            when(operationsService.getConfigurationFilePath(any())).thenReturn("foo");
            when(operationsService.runCommand(any())).thenReturn(Stream.of("bar\n"));

            mockMvc.perform(get(CONFIGURATION_FILE_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("bar")));
        }


        @Test
        @DisplayName("When configuration file is not required then return 412 Precondition Failed")
        void configurationFileIsNotRequired() throws Exception {
            String errMessage = "foo";
            InvalidAttributesException exception = new InvalidAttributesException(errMessage);

            when(operationsService.getConfigurationFilePath(any())).thenThrow(exception);

            mockMvc.perform(get(CONFIGURATION_FILE_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getConfigurationFilePath(any())).thenThrow(RuntimeException.class);

            mockMvc.perform(get(CONFIGURATION_FILE_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class SetConfigurationFileTestCase {
        @Test
        @DisplayName("When configuration file is updatable then return 200 OK")
        void configurationFileIsRequired() throws Exception {
            when(operationsService.getConfigurationFilePath(any())).thenReturn("foo");

            mockMvc.perform(put(CONFIGURATION_FILE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    asJsonString("foo")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When configuration file is not updatable then return 412 Precondition Failed")
        void configurationFileIsNotRequired() throws Exception {
            String errMessage = "foo";
            InvalidAttributesException exception = new InvalidAttributesException(errMessage);

            when(operationsService.getConfigurationFilePath(any())).thenThrow(exception);

            mockMvc.perform(put(CONFIGURATION_FILE_PATH)
                            .content(asJsonString("foo"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getConfigurationFilePath(any())).thenThrow(RuntimeException.class);

            mockMvc.perform(get(CONFIGURATION_FILE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString("foo")))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class ExecuteActionTestCase {

        @Test
        @DisplayName("When action has been executed then return 200 OK and response")
        void actionExecuted() throws Exception {
            Long expectedPid = 123L;
            String expectedWeaponName = "foo";

            Process process = mock(Process.class);
            var weapon = Weapon
                    .builder()
                    .name(expectedWeaponName)
                    .build();

            var weaponProcess = WeaponProcess
                    .builder()
                    .process(process)
                    .weapon(weapon)
                    .build();

            when(process.pid()).thenReturn(expectedPid);
            when(operationsService.executeAction(any())).thenReturn(weaponProcess);

            mockMvc.perform(post(EXECUTE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pid", is(expectedPid.intValue())))
                    .andExpect(jsonPath("$.weapon", is(expectedWeaponName)));
        }

        @Test
        @DisplayName("When user input is invalid then return 412 Precondition Failed")
        void invalidUserInput() throws Exception {
            String expectedErrMessage = "foo";
            Exception expectedException = new InvalidAttributesException(expectedErrMessage);

            when(operationsService.executeAction(any())).thenThrow(expectedException);

            mockMvc.perform(post(EXECUTE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(expectedErrMessage)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            Exception expectedException = new RuntimeException();

            when(operationsService.executeAction(any())).thenThrow(expectedException);

            mockMvc.perform(post(EXECUTE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetLaunchedActionsTestCase {

        @Test
        @DisplayName("When actions list has been built with only finalized actions then return 200 OK and response")
        void finalizedActionsResponse() throws Exception {
            ProcessHandle.Info mockInfo = Mockito.mock(ProcessHandle.Info.class);
            Process process = Mockito.mock(Process.class);
            Weapon weapon = Weapon.builder().name("foobar").build();
            File file = Mockito.mock(File.class);
            Instant expectedInstant = Instant.now();

            when(mockInfo.startInstant()).thenReturn(Optional.of(expectedInstant));
            when(mockInfo.commandLine()).thenReturn(Optional.of("bar"));
            when(process.info()).thenReturn(mockInfo);
            when(process.pid()).thenReturn(123L);

            WeaponProcessDTO weaponProcessDTO = WeaponProcessDTO.builder()
                    .weapon(weapon)
                    .process(process)
                    .outputFile(file)
                    .build();

            var mockWeaponProcessOne = new WeaponProcess(weaponProcessDTO);

            when(operationsService.getFinalizedActions()).thenReturn(Map.of(1L, mockWeaponProcessOne));
            when(operationsService.getRunningActions()).thenReturn(Map.of());

            mockMvc.perform(get(LAUNCHED_ACTIONS_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finalizedActions[0].pid", is(123)))
                    .andExpect(jsonPath("$.finalizedActions[0].weapon", is("foobar")))
                    .andExpect(jsonPath("$.finalizedActions[0].commandLine", is("bar")))
                    .andExpect(jsonPath("$.finalizedActions[0].executionDate", is(expectedInstant.toString())))
                    .andExpect(jsonPath("$.runningActions", hasSize(0)));
        }

        @Test
        @DisplayName("When actions list has been built with only running actions then return 200 OK and response")
        void runningActionsResponse() throws Exception {
            ProcessHandle.Info mockInfo = Mockito.mock(ProcessHandle.Info.class);
            Process process = Mockito.mock(Process.class);
            Weapon weapon = Weapon.builder().name("foobar").build();
            File file = Mockito.mock(File.class);
            Instant expectedInstant = Instant.now();

            when(mockInfo.startInstant()).thenReturn(Optional.of(expectedInstant));
            when(mockInfo.commandLine()).thenReturn(Optional.of("bar"));
            when(process.info()).thenReturn(mockInfo);
            when(process.pid()).thenReturn(123L);

            WeaponProcessDTO weaponProcessDTO = WeaponProcessDTO.builder()
                    .weapon(weapon)
                    .process(process)
                    .outputFile(file)
                    .build();

            var mockWeaponProcessOne = new WeaponProcess(weaponProcessDTO);

            when(operationsService.getFinalizedActions()).thenReturn(Map.of());
            when(operationsService.getRunningActions()).thenReturn(Map.of(1L, mockWeaponProcessOne));

            mockMvc.perform(get(LAUNCHED_ACTIONS_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.runningActions[0].pid", is(123)))
                    .andExpect(jsonPath("$.runningActions[0].weapon", is("foobar")))
                    .andExpect(jsonPath("$.runningActions[0].commandLine", is("bar")))
                    .andExpect(jsonPath("$.runningActions[0].executionDate", is(expectedInstant.toString())))
                    .andExpect(jsonPath("$.finalizedActions", hasSize(0)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getFinalizedActions()).thenThrow(RuntimeException.class);

            mockMvc.perform(get(LAUNCHED_ACTIONS_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class ReadActionStandardOutTestCase {

        @Test
        @DisplayName("When launched action output has been read then return 200 OK and response")
        void response() throws Exception {
            when(operationsService.getProcessPathname(any())).thenReturn("foo");
            when(operationsService.runCommand(any())).thenReturn(Stream.of("foobar\n"));

            mockMvc.perform(get(STDOUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("foobar")));
        }

        @Test
        @DisplayName("When pid input by user is invalid then return 412 Precondition Failed")
        void invalidPid() throws Exception {
            String errMessage = "foo";

            when(operationsService.getProcessPathname(any())).thenThrow(new InvalidAttributesException(errMessage));

            mockMvc.perform(get(STDOUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("When error occurred running service's runCommand method then return 503 Service Unavailable")
        void errorReadingResult() throws Exception {
            String errMessage = "foo";

            when(operationsService.runCommand(any())).thenThrow(new IOException(errMessage));

            mockMvc.perform(get(STDOUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getProcessPathname(any())).thenThrow(new RuntimeException());

            mockMvc.perform(get(STDOUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetActionOutputTestCase {

        @Test
        @DisplayName("when requested action has outputs then return 200 OK")
        void actionHasOutput() throws Exception {
            String[] expectedList = {"foobar", "baz", "quz"};

            when(operationsService.getProcessDirectoryPathname(any())).thenReturn("foo");
            when(operationsService.runCommand(any())).thenReturn(Stream.of(expectedList));

            mockMvc.perform(get(ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.files", is(List.of(expectedList))));
        }

        @Test
        @DisplayName("when requested action has no outputs then return 204 No Content")
        void actionHasNoOutput() throws Exception {
            when(operationsService.getProcessDirectoryPathname(any())).thenReturn("foo");
            when(operationsService.runCommand(any())).thenReturn(Stream.of());

            mockMvc.perform(get(ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("when requested action is an invalid PID then return 412 Precondition Failed")
        void pidNotValid() throws Exception {
            String errMessage = "foo";

            when(operationsService.getProcessDirectoryPathname(any())).thenThrow(new InvalidAttributesException(errMessage));

            mockMvc.perform(get(ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$.error", is(errMessage)));
        }

        @Test
        @DisplayName("when error while retrieving output list then return 503 Service Unavailable")
        void executionFailed() throws Exception {
            String errMessage = "foo";

            when(operationsService.getProcessDirectoryPathname(any())).thenReturn("foo");
            when(operationsService.runCommand(any())).thenThrow(new IOException(errMessage));

            mockMvc.perform(get(ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.error", is(errMessage)));
        }

        @Test
        @DisplayName("when unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getProcessDirectoryPathname(any())).thenThrow(RuntimeException.class);

            mockMvc.perform(get(ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class ReadActionOutputFileTestCase {

        @Test
        @DisplayName("when action output has been read then return 200 OK")
        void actionOutputIsRead() throws Exception {
            String expectedResult = "foobar";

            when(operationsService.getProcessDirectoryPathname(any())).thenReturn("foo");
            when(operationsService.readFile(any())).thenReturn("foobar");

            mockMvc.perform(get(READ_ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is(expectedResult)));
        }

        @Test
        @DisplayName("when getProcessDirectoryPathname service throws InvalidAttributesException then return 412 Precondition Failed")
        void invalidPid() throws Exception {
            String errMessage = "foo";

            when(operationsService.getProcessDirectoryPathname(any())).thenThrow(new InvalidAttributesException(errMessage));

            mockMvc.perform(get(READ_ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("when readFile service throws InvalidAttributesException then return 412 Precondition Failed")
        void readFileFailed() throws Exception {
            String errMessage = "foo";

            when(operationsService.readFile(any())).thenThrow(new InvalidAttributesException(errMessage));

            mockMvc.perform(get(READ_ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("when unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getProcessDirectoryPathname(any())).thenThrow(RuntimeException.class);

            mockMvc.perform(get(READ_ACTION_OUTPUT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class GetCurrentActionsTestCase {

        @Test
        @DisplayName("When actions list has been built with only running actions then return 200 OK and response")
        void runningActionsResponse() throws Exception {
            ProcessHandle.Info mockInfo = Mockito.mock(ProcessHandle.Info.class);
            Process process = Mockito.mock(Process.class);
            Weapon weapon = Weapon.builder().name("foobar").build();
            File file = Mockito.mock(File.class);
            Instant expectedInstant = Instant.now();

            when(mockInfo.startInstant()).thenReturn(Optional.of(expectedInstant));
            when(mockInfo.commandLine()).thenReturn(Optional.of("bar"));
            when(process.info()).thenReturn(mockInfo);
            when(process.pid()).thenReturn(123L);

            WeaponProcessDTO weaponProcessDTO = WeaponProcessDTO.builder()
                    .weapon(weapon)
                    .process(process)
                    .outputFile(file)
                    .build();

            var mockWeaponProcessOne = new WeaponProcess(weaponProcessDTO);

            when(operationsService.getRunningActions()).thenReturn(Map.of(1L, mockWeaponProcessOne));

            mockMvc.perform(get(LAUNCHED_ACTIVE_ACTIONS_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.runningActions[0].pid", is(123)))
                    .andExpect(jsonPath("$.runningActions[0].weapon", is("foobar")))
                    .andExpect(jsonPath("$.runningActions[0].commandLine", is("bar")))
                    .andExpect(jsonPath("$.runningActions[0].executionDate", is(expectedInstant.toString())));
        }

        @Test
        @DisplayName("When unexpected error then return 500 Internal Server Error")
        void error() throws Exception {
            when(operationsService.getRunningActions()).thenThrow(RuntimeException.class);

            mockMvc.perform(get(LAUNCHED_ACTIVE_ACTIONS_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class KillCurrentActionTestCase {

        @Test
        @DisplayName("when service is invoked then return 200 OK")
        void actionKilled() throws Exception {
            doNothing().when(operationsService).killRunningAction(any());

            mockMvc.perform(delete(KILL_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("when service throws IAE then return 412 Precondition Failed")
        void invalidAttributesExceptionThrown() throws Exception {
            String errMessage = "foo";
            doThrow(new InvalidAttributesException(errMessage)).when(operationsService).killRunningAction(any());

            mockMvc.perform(delete(KILL_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("when services throws Exception then return 500 Internal Service Error")
        void exceptionThrown() throws Exception {
            doThrow(RuntimeException.class).when(operationsService).killRunningAction(any());

            mockMvc.perform(delete(KILL_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    class InputIntoRunningActionTestCase {

        @Test
        @DisplayName("when service is successfully invoked then return 200 OK")
        void inputWritten() throws Exception {
            doNothing().when(operationsService).inputToProcess(any(), any());

            mockMvc.perform(put(WRITE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("when service throws IAE then return 412 Precondition Failed")
        void invalidAttributesExceptionThrown() throws Exception {
            String errMessage = "foo";
            doThrow(new InvalidAttributesException(errMessage)).when(operationsService).inputToProcess(any(), any());

            mockMvc.perform(put(WRITE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isPreconditionFailed())
                    .andExpect(jsonPath("$", is(errMessage)));
        }

        @Test
        @DisplayName("when service throws Exception then return 500 Internal Service Error")
        void exceptionThrown() throws Exception {
            doThrow(RuntimeException.class).when(operationsService).inputToProcess(any(), any());

            mockMvc.perform(put(WRITE_ACTION_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    private static String asJsonString(Object request) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(request);
    }
}