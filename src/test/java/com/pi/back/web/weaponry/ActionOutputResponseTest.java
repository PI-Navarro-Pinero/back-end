package com.pi.back.web.weaponry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ActionOutputResponseTest {

    @Test
    @DisplayName("when String list is provided then return ActionOutputResponse with 'files' containing given list")
    void newInstanceTest() {
        List<String> expectedList = List.of("foo", "bar");

        ActionOutputResponse actual = ActionOutputResponse.newInstance(expectedList);

        assertThat(actual.getFiles()).isEqualTo(expectedList);
        assertThat(actual.getError()).isNull();
    }

    @Test
    @DisplayName("when String is provided then return ActionOutputResponse with 'error' containing given String")
    void newErrorInstanceTest() {
        String expectedMessage = "foo";

        ActionOutputResponse actual = ActionOutputResponse.newErrorInstance(expectedMessage);

        assertThat(actual.getError()).isEqualTo(expectedMessage);
        assertThat(actual.getFiles()).isNull();
    }
}