package com.pi.back.web;

import com.pi.back.services.SystemService;
import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import com.pi.back.web.weaponry.ActionResponse;
import com.pi.back.web.weaponry.ActionsResponse;
import com.pi.back.web.weaponry.WeaponResponse;
import com.pi.back.web.weaponry.WeaponsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_AGENT;

@RestController
public class WeaponryController {

    private final SystemService systemService;

    @Autowired
    public WeaponryController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry")
    public ResponseEntity<WeaponsResponse> fetchWeaponry() {
        List<Weapon> weaponMap = systemService.getAvailableWeapons();

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
            Weapon weapon = systemService.getWeapon(weaponId);
            return ResponseEntity.ok(WeaponResponse
                    .newInstance(weaponId, weapon));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(WeaponResponse.newErrorInstance(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/weaponry/{weaponId}/configuration-file")
    public ResponseEntity<String> getConfigurationFile(@PathVariable(name = "weaponId") Integer weaponId,
                                                       @RequestParam(value = "encode", required = false, defaultValue = "0") Boolean encode) {
        try {
            String pathname = systemService.getConfigurationFilePath(weaponId);
            String command = "cat " + pathname;
            String result = systemService.runCommand(command);
            if (encode)
                result = new String(Base64.getEncoder().encode(result.getBytes()));
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
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
            String pathname = systemService.getConfigurationFilePath(weaponId);
            if (encoded)
                configurationFile = new String(Base64.getDecoder().decode(configurationFile.getBytes()));
            systemService.writeFile(pathname, configurationFile);
            return ResponseEntity.ok().build();
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
            WeaponProcess weaponProcess = systemService.runAction(weaponId, actionId, parameters);
            return ResponseEntity.ok().body(ActionResponse.newInstance(weaponProcess));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(ActionResponse.newErrorInstance(e));
        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ActionResponse.newErrorInstance(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions")
    public ResponseEntity<ActionsResponse> getLaunchedActions() {
        try {
            List<ActionResponse> finalizedActionResponseList = systemService.getFinalizedActions()
                    .values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());

            List<ActionResponse> runningActionResponseList = systemService.getRunningActions()
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
            String pathname = systemService.getProcessPathname(pid);
            String command = "tail" +
                    (lines == null ? (" +0") : (" -" + lines))
                    + " " + pathname;
            String result = systemService.runCommand(command);
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files")
    public ResponseEntity<String> getActionOutput(@PathVariable(name = "pid") Long pid) {
        try {
            String command = "ls -I " + pid + " " + systemService.getProcessDirectoryPathname(pid);
            String result = systemService.runCommand(command);

            if (result.isBlank())
                return ResponseEntity.noContent().build();

            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/{pid}/files/{fileName}")
    public ResponseEntity<String> readActionOutputFile(@PathVariable(name = "pid") Long pid,
                                                       @PathVariable(name = "fileName") String fileName) {
        try {
            String pathname = systemService.getProcessDirectoryPathname(pid) + "/" + fileName;
            String result = systemService.readFile(pathname);
            return ResponseEntity.ok().body(result);
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @GetMapping("/launched-actions/active")
    public ResponseEntity<ActionsResponse> getCurrentRunningActions() {
        try {
            List<ActionResponse> actionResponseList = systemService.getRunningActions()
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
            systemService.killRunningAction(pid);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_AGENT)
    @PutMapping("/launched-actions/active/{pid}/input")
    public ResponseEntity<String> inputIntoRunningAction(@PathVariable(name = "pid") Long pid,
                                                         @RequestParam(name = "input", required = false) String input) {
        try {
            systemService.inputToProcess(pid, input);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

