package com.pi.back.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LauncherDTO {
    private Integer weaponId;
    private Integer actionId;
    private List<String> parameters;

    public static LauncherDTO of(ExecuteActionDTO dto) {
        return LauncherDTO
                .builder()
                .weaponId(dto.getWeaponId())
                .actionId(dto.getActionId())
                .parameters(dto.getParameters())
                .build();
    }
}
