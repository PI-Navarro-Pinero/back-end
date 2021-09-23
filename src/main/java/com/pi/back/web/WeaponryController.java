package com.pi.back.web;

import com.pi.back.services.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pi.back.config.security.Privileges.Roles.ROLE_X;

@RestController
public class WeaponryController {

    private final SystemService systemService;

    @Autowired
    public WeaponryController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Secured(ROLE_X)
    @GetMapping("/weaponry/{weaponId}/actions/{actionId}")
    public ResponseEntity<HttpStatus> executeAction(@PathVariable(name = "weaponId") Integer weaponId,
                                                    @PathVariable(name = "actionId") Integer actionId,
                                                    @RequestParam(name = "parameters", required = false) List<String> parameters) {
        try {
            boolean executionResult = systemService.run(weaponId, actionId, parameters);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

