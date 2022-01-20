package com.pi.back.utils;

import com.pi.back.weaponry.dto.ExecuteActionDTO;
import com.pi.back.weaponry.dto.LauncherDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LauncherDTOTest {

    @Test
    void ofTest() {
        ExecuteActionDTO actionDTO = ExecuteActionDTO.builder()
                .weaponId(1)
                .actionId(2)
                .parameters(List.of("foo", "bar"))
                .build();

        LauncherDTO actual = LauncherDTO.of(actionDTO);

        assertThat(actual.getWeaponId()).isEqualTo(1);
        assertThat(actual.getActionId()).isEqualTo(2);
        assertThat(actual.getParameters()).isEqualTo(List.of("foo", "bar"));
    }
}