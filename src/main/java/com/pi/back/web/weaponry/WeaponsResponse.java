package com.pi.back.web.weaponry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeaponsResponse {
    private List<WeaponResponse> weaponry;
}
