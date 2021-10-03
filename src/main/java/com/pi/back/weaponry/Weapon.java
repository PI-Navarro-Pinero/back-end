package com.pi.back.weaponry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Weapon {
    private String name;
    private String description;
    private String configurationFile;
    private List<String> actions;
}
