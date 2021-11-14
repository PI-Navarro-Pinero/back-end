package com.pi.back.weaponry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
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
}
