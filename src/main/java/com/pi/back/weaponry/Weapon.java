package com.pi.back.weaponry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Slf4j
public class Weapon {
    private String name;
    private String description;
    private String configurationFile;
    private List<String> actions;

    public String getConfigurationFile() {
        try {
            return Objects.requireNonNull(configurationFile);
        }
        catch (NullPointerException e) {
            return "";
        }
    }

    public void setName(String name) {
        if(name == null) {
            log.error("Yaml file error: Weapon's name cannot be empty");
            throw new NullPointerException();
        }
        this.name = name;
    }

    public void setDescription(String description) {
        if(description == null) {
            log.error("Yaml file error: Weapon's description cannot be empty");
            throw new NullPointerException();
        }
        this.description = description;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    public void setActions(List<String> actions) {
        if(actions == null || actions.contains(null)) {
            log.error("Yaml file error: Weapon's actions list cannot be empty");
            throw new NullPointerException();
        }
        this.actions = actions;
    }
}
