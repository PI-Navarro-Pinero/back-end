package com.pi.back.web.weaponry;

import com.pi.back.weaponry.Weapon;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class WeaponResponseTest {

    @Test
    void newInstanceTest() throws IOException {
        Weapon mockWeapon = Weapon.builder()
                .name("foo")
                .configFile(File.createTempFile("bar", null))
                .description("foobar")
                .actions(List.of("baz", "quz", "qux"))
                .build();

        WeaponResponse actual = WeaponResponse.newInstance(1, mockWeapon);

        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getName()).isEqualTo("foo");
        assertThat(actual.isConfigurationFile()).isTrue();
        assertThat(actual.getDescription()).isEqualTo("foobar");
        assertThat(actual.getActions()).isEqualTo(Map.of(0, "baz", 1, "quz", 2, "qux"));
        assertThat(actual.getError()).isNull();
    }

    @Test
    void newErrorInstanceTest() {
        WeaponResponse actual = WeaponResponse.newErrorInstance("foo");

        assertThat(actual.getError()).isEqualTo("foo");
        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isNull();
        assertThat(actual.isConfigurationFile()).isFalse();
        assertThat(actual.getDescription()).isNull();
        assertThat(actual.getActions()).isNull();
    }
}