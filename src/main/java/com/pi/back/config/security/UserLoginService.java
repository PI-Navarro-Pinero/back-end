package com.pi.back.config.security;

import com.pi.back.users.User;
import com.pi.back.users.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserLoginService {

    private final UsersRepository usersRepository;

    @Autowired
    public UserLoginService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public Optional<User> findByUsername(String username) {
        final Optional<User> user = this.usersRepository.findByUsername(username);
        log.info("Authentication for user '{}' requested.", username);

        if (user.isEmpty())
            log.info("User '{}' not found.", username);

        return user;
    }
}
