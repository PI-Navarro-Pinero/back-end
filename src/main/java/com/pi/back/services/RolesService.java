package com.pi.back.services;

import com.pi.back.db.Role;
import com.pi.back.db.RolesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public List<Role> findAll() {
        final List<Role> roles = rolesRepository.findAll();
        log.info("{} roles found", roles.size());
        return roles;
    }
}
