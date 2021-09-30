package com.pi.back.weaponry;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class Action {
    private final Map<Integer, String> actionsMap;
}
