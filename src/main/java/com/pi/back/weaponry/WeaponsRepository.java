package com.pi.back.weaponry;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;
import java.util.Optional;

@Data
@Component
public class WeaponsRepository {

    private Weaponry weaponsList;

    @Autowired
    public WeaponsRepository(Weaponry weaponsList) {
        this.weaponsList = weaponsList;
    }

    public String getWeaponName(Integer weaponId) {
        return findWeapon(weaponId)
                .map(Weapon::getName)
                .orElse(null);
    }

    public Optional<String> getActionModel(Integer weaponId, Integer actionId) {
        try {
            return findAction(weaponId)
                    .map(actionsMap -> actionsMap.get(actionId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Weapon> findWeapon(Integer weaponId) {
        try {
            return Optional.ofNullable(weaponsList.getWeaponry().get(weaponId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getConfigurationFilePath(Integer weaponId) throws InvalidAttributesException {
        Optional<Weapon> optionalWeapon = findWeapon(weaponId);
        if (optionalWeapon.isEmpty())
            throw new InvalidAttributesException("Weapon " + weaponId + " do not exists");

        return optionalWeapon.map(Weapon::getConfigurationFile);
    }

    private Optional<List<String>> findAction(Integer weaponId) {
        return findWeapon(weaponId)
                .map(Weapon::getActions);
    }
}
