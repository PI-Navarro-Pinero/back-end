package com.pi.back.web;

import com.pi.back.services.SystemService;
import com.pi.back.weaponry.Weapon;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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
        Map<Integer, Weapon> weaponMap = systemService.getAvailableWeapons();

        final List<WeaponResponse> weaponsListResponse = weaponMap.entrySet().stream()
                .map(entry -> WeaponResponse.newInstance(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        if (weaponsListResponse.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(WeaponsResponse
                .builder()
                .weapons(weaponsListResponse)
                .build());
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/{weaponId}/actions")
    public ResponseEntity<WeaponResponse> fetchWeaponActions(@PathVariable(name = "weaponId") Integer weaponId) {
        try {
            Weapon weapon = systemService.getWeapon(weaponId);
            return ResponseEntity.ok(WeaponResponse
                    .newActionsInstance(weaponId, weapon));
        } catch (InvalidAttributesException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(WeaponResponse.newErrorInstance(e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/{weaponId}/actions/{actionId}")
    public ResponseEntity<String> executeAction(@PathVariable(name = "weaponId") Integer weaponId,
                                                @PathVariable(name = "actionId") Integer actionId,
                                                @RequestParam(name = "parameters", required = false) List<String> parameters) {
        try {
            boolean executionResult = systemService.run(weaponId, actionId, parameters);
            return ResponseEntity.ok().build();
        } catch (InvalidAttributesException e) {
            return ResponseEntity.badRequest().body(e.getExplanation());
        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

