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
public class ActionsResponse {
    private List<ActionResponse> runningActions;

    public static ActionsResponse newInstance(List<ActionResponse> actionResponseList) {
        return ActionsResponse.builder()
                .runningActions(actionResponseList)
                .build();
    }
}
