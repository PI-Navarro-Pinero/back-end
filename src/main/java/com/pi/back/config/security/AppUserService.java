package com.pi.back.config.security;

import com.pi.back.db.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class AppUserService implements UserDetailsService {

    private final UserLoginService userLoginService;

    @Autowired
    public AppUserService(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        final User user = userLoginService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User '%s' not found.", username)));

        return new AppUserDetails(user);
    }
}
