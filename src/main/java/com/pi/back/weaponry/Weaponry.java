package com.pi.back.weaponry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weaponry {
    private List<Weapon> weaponry;

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        weaponry.forEach(weapon -> s.append(weapon.toString()));
        return s.toString();
    }
}
