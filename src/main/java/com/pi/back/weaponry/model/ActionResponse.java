package com.pi.back.weaponry.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.weaponry.WeaponProcess;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionResponse {
    private Long pid;
    private String weapon;
    private String executionDate;
    private String commandLine;

    public static ActionResponse newInstance(WeaponProcess weaponProcess) {
        return ActionResponse.builder()
                .pid(weaponProcess.pid())
                .weapon(weaponProcess.getWeapon().getName())
                .build();
    }

    public static ActionResponse newStatusInstance(WeaponProcess weaponProcess) {
        return ActionResponse.builder()
                .pid(weaponProcess.pid())
                .weapon(weaponProcess.getWeapon().getName())
                .executionDate(weaponProcess.getCreationTime())
                .commandLine(weaponProcess.getCommandLine())
                .build();
    }
}
