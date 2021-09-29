package com.pi.back.cmd;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class ActionsManager {

    Map<Integer, Map<Integer, String>> actionsFilesMap;

    public ActionsManager() {
        this.actionsFilesMap = new HashMap<>();
    }

    public void updateActionsMap(Integer i, Map<Integer, String> m) {
        actionsFilesMap.put(i, m);
    }

    public String queryActionsMap(Integer weaponId, Integer actionId) {
        return actionsFilesMap.get(weaponId).get(actionId);
    }
}
