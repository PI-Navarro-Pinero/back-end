package com.pi.back.weaponry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteActionDTO {
    private Integer weaponId;
    private Integer actionId;
    private List<String> parameters;
}
