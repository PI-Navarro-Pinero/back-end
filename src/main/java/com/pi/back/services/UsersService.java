package com.pi.back.services;

import com.pi.back.db.User;
import com.pi.back.db.UsersRepository;
import com.pi.back.web.users.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> findAll() {
        final List<User> users = usersRepository.findAll();
        log.info("{} users found", users.size());
        return users;
    }

    public User findUser(int userId) {
        return usersRepository.findUser(userId);
    }

    public User create(UserRequest request) throws InvalidAttributesException {

        final User newUser = User.builder()
                .username(request.getUsername())
                .password("_" + request.getUsername())
                .fullname(request.getFullname())
                .license(request.getLicense())
                .roles(request.getPrivileges())
                .build();

        final List<User> allUsers = findAll();

        Predicate<User> condition = user ->
                user.getUsername().equalsIgnoreCase(request.getUsername())
                || user.getLicense().equals(request.getLicense());

        final boolean conditionMeets = allUsers.stream().anyMatch(condition);
        if (conditionMeets) {
            List<Integer> users = allUsers.stream().filter(condition).map(User::getId).collect(Collectors.toList());
            throw new InvalidAttributesException("Requested user creation failed: " +
                    "A user with the same username or license already exists: " + users);
        }

        return this.usersRepository.save(newUser);
    }

    public User update(UserRequest request) throws Exception {

        final User userToUpdate = User.builder()
                .id(request.getId())
                .username(request.getUsername())
                .password("_" + request.getUsername())
                .fullname(request.getFullname())
                .license(request.getLicense())
                .roles(request.getPrivileges())
                .build();

        if (userToUpdate.getId().equals(0) || userToUpdate.getId() == null) {
            log.info("Null ID provided for user updating.");
            throw new InvalidParameterException("Requested user update failed: Invalid ID.");
        }

        final Predicate<User> condition = user ->
                user.getUsername().equalsIgnoreCase(userToUpdate.getUsername())
                        && user.getLicense().equals(userToUpdate.getLicense())
                        && !user.getId().equals(userToUpdate.getId());

        final List<User> allUsers = findAll();

        validateUserExistence(allUsers, userToUpdate.getId());

        final boolean conditionMeets = allUsers.stream().anyMatch(condition);
        if (conditionMeets) {
            List<Integer> users = allUsers.stream().filter(condition).map(User::getId).collect(Collectors.toList());
            throw new InvalidAttributesException("Requested user update failed: " +
                    "A user with the same username, cuil or email already exists: " + users);
        }

        return this.usersRepository.save(userToUpdate);
    }

    private void validateUserExistence(List<User> allUsers, int userToUpdateId) throws ClassNotFoundException {
        final Optional<User> optionalUser = allUsers.stream()
                .filter(u -> u.getId() == userToUpdateId)
                .findFirst();

        if (optionalUser.isEmpty()) {
            throw new ClassNotFoundException("Requested user update failed: User to update not exists.");
        }
    }
}
