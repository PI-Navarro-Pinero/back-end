package com.pi.back.web.weaponry;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionOutputResponse {
    private List<String> files;
    private String error;

    static public ActionOutputResponse newInstance(List<String> files) {
        return ActionOutputResponse.builder()
                .files(files)
                .build();
    }

    static public ActionOutputResponse newErrorInstance(String message) {
        return ActionOutputResponse.builder()
                .error(message)
                .build();
    }
}
