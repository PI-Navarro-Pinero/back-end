package com.pi.back.weaponry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class WeaponTest {

    @Nested
    class RetrieveActionTestCase {

        @Test
        @DisplayName("when action exists then return as optional")
        void actionExist() {
            List<String> actions = List.of("foo", "bar", "foobar", "baz");

            Weapon weapon = Weapon.builder()
                    .actions(actions)
                    .build();

            for (int i = 0; i < weapon.getActions().size(); i++) {
                assertThat(weapon.retrieveAction(i)).isEqualTo(Optional.of(actions.get(i)));
            }
        }

        @Test
        @DisplayName("when action id does not match then return empty optional")
        void actionNotExist() {
            List<String> actions = List.of("foo", "bar");

            Weapon weapon = Weapon.builder()
                    .actions(actions)
                    .build();

            assertThat(weapon.retrieveAction(actions.size() + 1)).isEqualTo(Optional.empty());
        }
    }
}