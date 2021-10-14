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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pi.back.config.security.Privileges.Roles.ROLE_X;

@RestController
public class WeaponryController {

    private final SystemService systemService;

    @Autowired
    public WeaponryController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Secured(ROLE_X)
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

    @Secured(ROLE_X)
    @GetMapping("/weaponry/{weaponId}/actions")
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

    @Secured(ROLE_X)
    @GetMapping("/weaponry/{weaponId}/actions/{actionId}")
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

    @Secured(ROLE_X)
    @GetMapping("/weaponry/running-actions")
    public ResponseEntity<ActionsResponse> runningActions() {
        try {
            Map<Long, WeaponProcess> runningActions = systemService.getRunningActions();
            List<ActionResponse> actionResponseList = runningActions.values().stream()
                    .map(ActionResponse::newStatusInstance)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(ActionsResponse.newInstance(actionResponseList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/running-actions/{pid}/terminate")
    public ResponseEntity<String> killRunningAction(@PathVariable(name = "pid") Long pid) {
        try {
            systemService.killRunningAction(pid);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/running-actions/{pid}/output")
    public ResponseEntity<String> actionOutput(@PathVariable(name = "pid") Long pid,
                                               @RequestParam(name = "lines", required = false) Integer lines) {
        try {
            String pathname = systemService.getProcessPathname(pid);
            String command = "tail" +
                    (lines == null ? (" +0") : (" -" + lines))
                    + " " + pathname;
            String result = systemService.runCommand(command);
            return ResponseEntity.ok().body(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/running-actions/{pid}/input")
    public ResponseEntity<String> actionInput(@PathVariable(name = "pid") Long pid,
                                              @RequestParam(name = "input", required = false) String input) {
        try {
            systemService.inputToProcess(pid, input);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

