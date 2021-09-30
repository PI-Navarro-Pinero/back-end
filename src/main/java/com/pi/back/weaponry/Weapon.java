package com.pi.back.weaponry;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Weapon {
    private final String name;
    private final String description;
    private final Action actions;
}
