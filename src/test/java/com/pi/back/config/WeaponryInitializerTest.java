package com.pi.back.config;

import com.pi.back.weaponry.Weapon;
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
        Weapon actualWeapon = actual.getWeaponry().get(0);

        assertThat(actual.getWeaponry().size()).isEqualTo(1);
        assertThat(actualWeapon.getName()).isEqualTo("Nmap");
        assertThat(actualWeapon.getDescription()).isEqualTo("Nmap tool for performing a ping-only scan");
        assertThat(actualWeapon.getActions().get(0)).isEqualTo("nmap -sn [target]");
    }

    @Test
    @DisplayName("when two weapons are correctly loaded, then creates two Weapon instances")
    void multipleWeapons() {
        File file = new File("src/test/resources/multipleWeapons.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);
        Weapon actualWeaponOne = actual.getWeaponry().get(0);
        Weapon actualWeaponTwo = actual.getWeaponry().get(1);

        assertThat(actual.getWeaponry().size()).isEqualTo(2);
        assertThat(actualWeaponOne.getName()).isEqualTo("Nmap");
        assertThat(actualWeaponOne.getDescription()).isEqualTo("Nmap tool for performing a ping-only scan");
        assertThat(actualWeaponOne.getActions().get(0)).isEqualTo("nmap -sn [target]");

        assertThat(actualWeaponTwo.getName()).isEqualTo("Aircrack-ng");
        assertThat(actualWeaponTwo.getDescription()).isEqualTo("Tools to assess WiFi network security");
        assertThat(actualWeaponTwo.getActions().get(0)).isEqualTo("airodump-ng [interface]");
        assertThat(actualWeaponTwo.getActions().get(1)).isEqualTo("airodump-ng --bssid [target MAC] [interface]");
    }

    @Test
    @DisplayName("either configuration file is set or not, then Weapon instances are created")
    void configurationFile() {
        File file = new File("src/test/resources/configFileOptional.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);
        Weapon actualWeaponOne = actual.getWeaponry().get(0);
        Weapon actualWeaponTwo = actual.getWeaponry().get(1);

        assertThat(actual.getWeaponry().size()).isEqualTo(2);
        assertThat(actualWeaponOne.getConfigurationFile()).isEqualTo("");
        assertThat(actualWeaponTwo.getConfigurationFile()).isEqualTo("/path/to/configuration/file");
    }

    @Test
    @DisplayName("when name is missing, then return null")
    void nameMissing() {
        File file = new File("src/test/resources/nameMissing.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("when description is missing, then return null")
    void descriptionMissing() {
        File file = new File("src/test/resources/descriptionMissing.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("when action is missing, then return null")
    void actionMissing() {
        File file = new File("src/test/resources/actionMissing.yaml");
        Yaml yaml = new Yaml(new Constructor(Weaponry.class));

        Weaponry actual = sut.loadYaml(file, yaml);

        assertThat(actual).isNull();
    }
}