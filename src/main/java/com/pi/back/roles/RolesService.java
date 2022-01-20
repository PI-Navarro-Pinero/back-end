package com.pi.back.roles;

import com.pi.back.config.security.Privileges;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {

    public List<Privileges> findAll() {
        return List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT);
    }
}
