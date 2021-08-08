package com.pi.back.services;

import com.pi.back.db.Users;
import com.pi.back.db.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> findAll() {
        final List<Users> users = usersRepository.findAll();
        log.info("{} users found", users.size());
        return users;
    }

    public Users findUser(int userId) {
        return usersRepository.findUser(userId);
    }
}
