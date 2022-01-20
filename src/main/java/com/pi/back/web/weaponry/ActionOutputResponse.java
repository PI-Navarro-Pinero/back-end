package com.pi.back.web.weaponry;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ActionOutputResponse {

    private List<String> files;

    static public ActionOutputResponse newInstance(List<String> files) {
        return ActionOutputResponse.builder()
                .files(files)
                .build();
    }
}
