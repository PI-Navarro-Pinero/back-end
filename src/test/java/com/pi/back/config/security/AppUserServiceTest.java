package com.pi.back.config.security;

import com.pi.back.db.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private UserLoginService userLoginService;

    @InjectMocks
    private AppUserService sut;

    private static final User MOCK_USER = User.builder()
            .id(1)
            .fullname("foo")
            .password("foobar")
            .roles(List.of(Privileges.ROLE_ADMIN))
            .username("bar")
            .license("baz")
            .build();

    @Test
    @DisplayName("when username is not found then throw UsernameNotFoundException")
    void usernameIsNotFound() {
        String username = "foo";

        when(userLoginService.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.loadUserByUsername(username))
                .isExactlyInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User '" + username + "' not found.");
    }

    @Test
    @DisplayName("when username is found then return AppUserDetails")
    void usernameIsFoundTest() {
        when(userLoginService.findByUsername(any())).thenReturn(Optional.of(MOCK_USER));

        var actual = sut.loadUserByUsername(any());

        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isEqualTo(MOCK_USER.getUsername());
        assertThat(actual.getPassword()).isEqualTo(MOCK_USER.getPassword());
        assertThat(actual.getAuthorities()).isEqualTo(Collections.singletonList(
                new SimpleGrantedAuthority(MOCK_USER.getRoles().get(0).getRole())));
        assertThat(actual.isAccountNonExpired()).isTrue();
        assertThat(actual.isAccountNonLocked()).isTrue();
        assertThat(actual.isCredentialsNonExpired()).isTrue();
        assertThat(actual.isEnabled()).isTrue();
    }
}