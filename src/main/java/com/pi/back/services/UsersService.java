package com.pi.back.services;

import com.pi.back.db.User;
import com.pi.back.db.UsersRepository;
import com.pi.back.web.users.UserDTO;
import com.pi.back.web.users.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.directory.InvalidAttributesException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        final List<User> users = usersRepository.findAll();
        log.info("{} " + ((users.size() == 1) ? "user" : "users") + " found in repository", users.size());
        return users;
    }

    public User findUser(Integer userId) throws NoSuchElementException {
        return usersRepository.findById(userId).orElseThrow();
    }

    public User create(UserRequest request) throws InvalidAttributesException {

        final User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode("_" + request.getUsername()))
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
            throw new InvalidAttributesException("Requested user creation failed: " +
                    "A user with the same username or license already exists");
        }

        return this.usersRepository.save(newUser);
    }

    public User update(UserDTO request) throws Exception {

        final User userToUpdate = User.builder()
                .id(request.getId())
                .username(request.getUsername())
                .password(passwordEncoder.encode("_" + request.getUsername()))
                .fullname(request.getFullname())
                .license(request.getLicense())
                .roles(request.getPrivileges())
                .build();

        if (userToUpdate.getId() == null) {
            log.error("Provided id for user updating was null.");
            throw new InvalidParameterException("Requested user update failed: Invalid ID.");
        }

        if (userToUpdate.getId().equals(0)) {
            log.error("Provided id for user updating was 0.");
            throw new InvalidParameterException("Requested user update failed: Invalid ID.");
        }

        final Predicate<User> condition = user ->
                (user.getUsername().equalsIgnoreCase(userToUpdate.getUsername())
                        || user.getLicense().equals(userToUpdate.getLicense()))
                        && !user.getId().equals(userToUpdate.getId());

        final List<User> allUsers = findAll();

        validateUserExistence(allUsers, userToUpdate.getId(),
                "Requested user update failed: User to update do not exists.");

        final boolean conditionMeets = allUsers.stream().anyMatch(condition);
        if (conditionMeets) {
            throw new InvalidAttributesException("Requested user update failed: " +
                    "A user with the same username or license already exists");
        }

        return this.usersRepository.save(userToUpdate);
    }

    public void delete(Integer userId) throws NoSuchElementException {
        final Optional<User> userToDelete = usersRepository.findById(userId);

        if (userToDelete.isEmpty()) {
            log.error("Non-existent user with id {} requested for deleting.", userId);
            throw new NoSuchElementException("Requested user deletion failed: User with such ID does not exist.");
        }

        if (userId == 0) {
            log.error("Forbidden ID (0) provided for user deleting.");
            throw new InvalidParameterException("Requested user deletion failed: Forbidden ID.");
        }

        usersRepository.delete(userToDelete.get());
    }

    private void validateUserExistence(List<User> allUsers, int userToUpdateId, String message) throws ClassNotFoundException {
        final Optional<User> optionalUser = allUsers.stream()
                .filter(u -> u.getId() == userToUpdateId)
                .findFirst();

        if (optionalUser.isEmpty()) {
            throw new ClassNotFoundException(message);
        }
    }
}
