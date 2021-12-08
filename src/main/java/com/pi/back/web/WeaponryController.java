package com.pi.back.web;

import com.pi.back.services.OperationsService;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import com.pi.back.web.weaponry.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_AGENT;

@RestController
public class WeaponryController {

    private final OperationsService operationsService;

    @Autowired
    public WeaponryController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry")
    public ResponseEntity<WeaponsResponse> fetchWeaponry() {
        List<Weapon> weaponMap = operationsService.getAvailableWeapons();

        AtomicInteger index = new AtomicInteger();
        final List<WeaponResponse> weaponsListResponse = weaponMap.stream()
                .map(weapon -> WeaponResponse.newInstance(index.getAndIncrement(), weapon))
                .collect(Collectors.toList());

        if (weaponsListResponse.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(WeaponsResponse
                .builder()
                .weaponry(weaponsListResponse)
                .build());
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry/{weaponId}")
    public ResponseEntity<WeaponResponse> fetchWeaponActions(@PathVariable(name = "weaponId") Integer weaponId) {
        try {
            Weapon weapon = operationsService.getWeapon(weaponId);
            return ResponseEntity.ok(WeaponResponse.newInstance(weaponId, weapon));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(WeaponResponse.newErrorInstance(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry/{weaponId}/configuration-file")
    public ResponseEntity<String> getConfigurationFile(@PathVariable(name = "weaponId") Integer weaponId,
                                                       @RequestParam(value = "encode", required = false, defaultValue = "0") Boolean encode) {
        try {
            String pathname = operationsService.getConfigurationFilePath(weaponId);
            String result = operationsService.runCommand("cat " + pathname)
                    .collect(Collectors.joining("\n"));
            if (encode)
                result = new String(Base64.getEncoder().encode(result.getBytes()));
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @PutMapping("/weaponry/{weaponId}/configuration-file")
    public ResponseEntity<String> setConfigurationFile(@PathVariable(name = "weaponId") Integer weaponId,
                                                       @RequestBody String configurationFile,
                                                       @RequestParam(required = false) boolean encoded) {
        try {
            String pathname = operationsService.getConfigurationFilePath(weaponId);
            if (encoded)
                configurationFile = new String(Base64.getDecoder().decode(configurationFile.getBytes()));
            operationsService.writeFile(pathname, configurationFile);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @PostMapping("/weaponry/{weaponId}/actions/{actionId}")
    public ResponseEntity<ActionResponse> executeAction(@PathVariable(name = "weaponId") Integer weaponId,
                                                        @PathVariable(name = "actionId") Integer actionId,
                                                        @RequestParam(name = "parameters", required = false) List<String> parameters) {
        try {
            WeaponProcess weaponProcess = operationsService.runAction(weaponId, actionId, parameters);
            return ResponseEntity.ok().body(ActionResponse.newInstance(weaponProcess));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ActionResponse.newErrorInstance(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ActionResponse.newErrorInstance(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions")
    public ResponseEntity<ActionsResponse> getLaunchedActions() {
        try {
            List<ActionResponse> finalizedActionResponseList = operationsService.getFinalizedActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            List<ActionResponse> runningActionResponseList = operationsService.getRunningActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            ActionsResponse response = ActionsResponse.builder()
                    .finalizedActions(finalizedActionResponseList)
                    .runningActions(runningActionResponseList)
                    .build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/stdout")
    public ResponseEntity<String> readActionStdout(@PathVariable(name = "pid") Long pid,
                                                   @RequestParam(name = "lines", required = false) Integer lines) {
        try {
            String pathname = operationsService.getProcessPathname(pid);
            String command = String.format("tail %s %s",
                    (lines == null ? ("+0") : ("-" + lines)),
                    pathname);
            String result = operationsService.runCommand(command)
                    .collect(Collectors.joining("\n"));
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files")
    public ResponseEntity<ActionOutputResponse> getActionOutput(@PathVariable(name = "pid") Long pid) {
        try {
            String command = String.format("ls %s", operationsService.getProcessDirectoryPathname(pid));
            List<String> result = operationsService.runCommand(command).collect(Collectors.toList());

            if (result.isEmpty())
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok(ActionOutputResponse.newInstance(result));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ActionOutputResponse.newErrorInstance(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ActionOutputResponse.newErrorInstance(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files/{fileName}")
    public ResponseEntity<String> readActionOutputFile(@PathVariable(name = "pid") Long pid,
                                                       @PathVariable(name = "fileName") String fileName) {
        try {
            String pathname = String.format("%s/%s", operationsService.getProcessDirectoryPathname(pid), fileName);
            String result = operationsService.readFile(pathname);
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/active")
    public ResponseEntity<ActionsResponse> getCurrentRunningActions() {
        try {
            List<ActionResponse> actionResponseList = operationsService.getRunningActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            ActionsResponse response = ActionsResponse.builder()
                    .runningActions(actionResponseList)
                    .build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @DeleteMapping("/launched-actions/active/{pid}/terminate")
    public ResponseEntity<String> killCurrentRunningAction(@PathVariable(name = "pid") Long pid) {
        try {
            operationsService.killRunningAction(pid);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @PutMapping("/launched-actions/active/{pid}/stdin")
    public ResponseEntity<String> inputIntoRunningAction(@PathVariable(name = "pid") Long pid,
                                                         @RequestParam(name = "stdin", required = false) String input) {
        try {
            operationsService.inputToProcess(pid, input);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

