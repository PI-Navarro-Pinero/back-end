package com.pi.back.config;

import com.pi.back.weaponry.Weaponry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class WeaponryInitializerTest {

    @InjectMocks
    WeaponryInitializer sut;

    @Test
    @DisplayName("when one weapon is correctly loaded, then creates one Weapon instance")
    void oneWeapon() {
        File file = new File("src/test/resources/oneWeapon.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);

        assertThat(actual.getWeaponry().size()).isEqualTo(1);
    }
}