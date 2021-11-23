package com.pi.back.weaponry;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Data
@Slf4j
@Component
public class WeaponsRepository {

    private Weaponry weaponsList;

    @Autowired
    public WeaponsRepository(Weaponry weaponsList) {
        this.weaponsList = weaponsList;
    }

    public Optional<Weapon> findWeapon(Integer weaponId) {
        try {
            return Optional.of(weaponsList.getWeaponry().get(weaponId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
