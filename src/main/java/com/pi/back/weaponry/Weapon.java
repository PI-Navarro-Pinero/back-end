package com.pi.back.weaponry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Slf4j
public class Weapon {

    private String name;
    private String description;
    private List<String> actions;
    private File configFile;

    public void setName(String name) {
        if (name == null) {
            log.error("Yaml file error: Weapon's name cannot be empty");
            throw new NullPointerException();
        }
        this.name = name;
    }

    public void setDescription(String description) {
        if (description == null) {
            log.error("Yaml file error: Weapon's description cannot be empty");
            throw new NullPointerException();
        }
        this.description = description;
    }

    public void setConfigurationFile(String configurationFile) {
        File file = new File(configurationFile);

        if (!file.exists() || !file.isFile()) {
            log.error("Yaml file error: {}'s configuration file is not valid.", this.name);
            throw new NullPointerException();
        }

        this.configFile = file;
    }

    public void setActions(List<String> actions) {
        if (actions == null || actions.contains(null)) {
            log.error("Yaml file error: Weapon's actions list cannot be empty");
            throw new NullPointerException();
        }
        this.actions = actions;
    }

    public Optional<String> retrieveAction(Integer actionId) {
        try {
            return Optional.of(actions.get(actionId));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "\n\n*\tName: %s\n" +
                "\tDescription: %s\n" +
                "\tConfiguration File: %s\n " +
                "\tActions: %s\n" +
                "\t\t---------------------------",
                name, description, configFile, actions.toString());
    }
}
