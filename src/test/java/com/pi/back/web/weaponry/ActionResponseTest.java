package com.pi.back.web.weaponry;

import com.pi.back.weaponry.Weapon;
import com.pi.back.weaponry.WeaponProcess;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActionResponseTest {

    @Test
    void newInstanceTest() {
        Process mockProcess = Mockito.mock(Process.class);
        when(mockProcess.pid()).thenReturn(123L);

        Weapon mockWeapon = Weapon.builder()
                .name("foo")
                .build();

        WeaponProcess weaponProcess = WeaponProcess.builder()
                .process(mockProcess)
                .weapon(mockWeapon)
                .build();

        ActionResponse actual = ActionResponse.newInstance(weaponProcess);

        assertThat(actual.getPid()).isEqualTo(weaponProcess.pid());
        assertThat(actual.getWeapon()).isEqualTo(mockWeapon.getName());
        assertThat(actual.getExecutionDate()).isNull();
        assertThat(actual.getCommandLine()).isNull();
        assertThat(actual.getError()).isNull();
    }

    @Test
    void newStatusInstanceTest() {
        Instant expectedInstant = Instant.now();
        String expectedCommandLine = "bar";
        Long expectedPid = 123L;
        String expectedWeaponName = "foo";

        Weapon mockWeapon = Weapon.builder()
                .name(expectedWeaponName)
                .build();

        WeaponProcess mockWP = mock(WeaponProcess.class);
        when(mockWP.pid()).thenReturn(expectedPid);
        when(mockWP.getCreationTime()).thenReturn(String.valueOf(expectedInstant));
        when(mockWP.getCommandLine()).thenReturn(expectedCommandLine);
        when(mockWP.getWeapon()).thenReturn(mockWeapon);

        ActionResponse actual = ActionResponse.newStatusInstance(mockWP);

        assertThat(actual.getPid()).isEqualTo(expectedPid);
        assertThat(actual.getWeapon()).isEqualTo(expectedWeaponName);
        assertThat(actual.getExecutionDate()).isEqualTo(expectedInstant.toString());
        assertThat(actual.getCommandLine()).isEqualTo(expectedCommandLine);
        assertThat(actual.getError()).isNull();
    }

    @Test
    void newErrorInstanceTest() {
        String expectedMessage = "foo";

        ActionResponse actual = ActionResponse.newErrorInstance(expectedMessage);

        assertThat(actual.getPid()).isNull();
        assertThat(actual.getWeapon()).isNull();
        assertThat(actual.getExecutionDate()).isNull();
        assertThat(actual.getCommandLine()).isNull();
        assertThat(actual.getError()).isEqualTo(expectedMessage);
    }
}