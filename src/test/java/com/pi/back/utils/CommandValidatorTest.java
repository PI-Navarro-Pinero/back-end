package com.pi.back.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CommandValidatorTest {

    @Test
    @DisplayName("when null inputs then throw IllegalArgumentException")
    void nullCommandModel_and_nullQueryParams() {
        assertThatThrownBy(() -> CommandValidator.buildCommand(null, null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("when null command model and empty query params list then throw IllegalArgumentException")
    void nullCommandModel_and_emptyQueryParams() {
        assertThatThrownBy(() -> CommandValidator.buildCommand(null, List.of()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("when null command and query params list then throw IllegalArgumentException")
    void nullCommandModel_and_queryParamsList() {
        assertThatThrownBy(() -> CommandValidator.buildCommand(null, List.of("foo")))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("when simple command model and query parameters is null then throw IllegalArgumentException")
    void notNullCommandModel_and_nullQueryParams() {
        assertThatThrownBy(() -> CommandValidator.buildCommand("foo", null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("when simple command model and empty query params list then return command")
    void simpleCommand_and_emptyQueryParams() {
        String command = "foo";

        Optional<String> actual = CommandValidator.buildCommand(command, List.of());

        assertThat(actual).isEqualTo(Optional.of(command));
    }

    @Test
    @DisplayName("when simple command model and any query params list then return command")
    void simpleCommand_and_anyList() {
        String command = "foo";

        Optional<String> actual = CommandValidator.buildCommand(command, List.of("bar", "foobar"));

        assertThat(actual).isEqualTo(Optional.of(command));
    }

    @Test
    @DisplayName("when parametrized command model and non matching count list then return empty optional")
    void parametrizedCommand_and_nonMatchingElementList() {
        String command = "foo {} {}";

        Optional<String> actual = CommandValidator.buildCommand(command, List.of("bar"));

        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("when parametrized command model and matching count list then return command")
    void parametrizedCommand_and_matchingElementList() {
        String command = "foo {} {}";

        Optional<String> actual = CommandValidator.buildCommand(command, List.of("bar", "foobar"));

        assertThat(actual).isEqualTo(Optional.of("foo bar foobar"));
    }
}