package com.pi.back.web.weaponry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pi.back.weaponry.Weapon;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeaponResponse {

    private Integer id;
    private String name;
    private String description;
    private Map<Integer, String> actions;
    private String error;

    public static WeaponResponse newInstance(Integer id, Weapon weapon) {
        AtomicInteger index = new AtomicInteger();

        return WeaponResponse.builder()
                .id(id)
                .name(weapon.getName())
                .description(weapon.getDescription())
                .actions(weapon.getActions().stream()
                        .collect(Collectors.toMap(i -> index.getAndIncrement(), s -> s)))
                .build();
    }

    public static WeaponResponse newErrorInstance(Exception error) {
        return WeaponResponse.builder()
                .error(error.getMessage())
                .build();
    }
}
