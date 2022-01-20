package com.pi.back.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationsTest {

    @Nested
    class NotNullNotEmptyTestCase {

        @Test
        @DisplayName("when empty input then throw IllegalArgumentException")
        void emptyInput() {
            final String input = "";
            assertThatThrownBy(() -> Validations.notNullNorEmpty(input, "foo"))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("foo cannot be empty");
        }

        @Test
        @DisplayName("when null input then throw IllegalArgumentException")
        void nullInput() {
            final String input = null;
            assertThatThrownBy(() -> Validations.notNullNorEmpty(input, "foo"))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("foo cannot be null");
        }

        @Test
        @DisplayName("when not empty nor null input then return value")
        void notEmptyNorNullInput() {
            final String value = "FooValue";
            final String actual = Validations.notNullNorEmpty(value, "foo");

            assertThat(actual).isEqualTo(value);
        }
    }

    @Nested
    class NotNullTestCase {

        @Test
        @DisplayName("when empty input then return value")
        void emptyInput() {
            final String value = "";
            final String actual = Validations.notNull(value, "foo");

            assertThat(actual).isEqualTo(value);
        }

        @Test
        @DisplayName("when not empty input then return value")
        void notEmptyInput() {
            final String value = "Foo";
            final String actual = Validations.notNull(value, "foo");

            assertThat(actual).isEqualTo(value);
        }

        @Test
        @DisplayName("when null input then throw IllegalArgumentException")
        void nullInput() {
            final String input = null;
            assertThatThrownBy(() -> Validations.notNull(input, "foo"))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("foo cannot be null");
        }
    }
}