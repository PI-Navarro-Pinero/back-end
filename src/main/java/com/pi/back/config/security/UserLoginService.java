package com.pi.back.config.security;

import com.pi.back.db.User;
import com.pi.back.db.UsersRepository;
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

        if (user.isEmpty()) {
            log.info("Authentication for '{}' failed. User not found.", username);
        } else {
            log.info("Authentication for user '{}' requested.", username);
        }

        return user;
    }
}
