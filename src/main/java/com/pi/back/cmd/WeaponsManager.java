package com.pi.back.cmd;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class WeaponsManager {

    private final Map<Integer, String> weaponsFilesMap;

    public WeaponsManager() {
        this.weaponsFilesMap = new HashMap<>();
    }

    public void updateWeaponsMap(Integer i, String s) {
        weaponsFilesMap.put(i, s);
    }

    public String queryWeaponsMap(Integer i) {
        return weaponsFilesMap.get(i);
    }
}
