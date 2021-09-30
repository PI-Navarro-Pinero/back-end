package com.pi.back.weaponry;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Data
public class WeaponsRepository {

    private final Map<Integer, Weapon> weaponsMap;

    public WeaponsRepository() {
        this.weaponsMap = new HashMap<>();
    }

    public void insert(Integer i, Object description) {
        weaponsMap.put(i, (Weapon) description);
    }

    public String getWeaponName(Integer weaponId) {
        return findWeapon(weaponId)
                .map(Weapon::getName)
                .orElse(null);
    }

    public Optional<String> getActionModel(Integer weaponId, Integer actionId) {
        return findAction(weaponId)
                .map(Action::getActionsMap)
                .map(actionsMap -> actionsMap.get(actionId));
    }

    public Optional<Weapon> findWeapon(Integer weaponId) {
        return Optional.ofNullable(weaponsMap.get(weaponId));
    }

    private Optional<Action> findAction(Integer weaponId) {
        return findWeapon(weaponId)
                .map(Weapon::getActions);
    }
}
