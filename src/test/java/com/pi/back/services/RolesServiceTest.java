package com.pi.back.services;

import com.pi.back.config.security.Privileges;
import com.pi.back.roles.RolesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @InjectMocks
    private RolesService sut;

    @Test
    void findAllTest() {
        List<Privileges> privilegesList = List.of(Privileges.ROLE_ADMIN, Privileges.ROLE_AGENT);

        assertThat(sut.findAll()).isEqualTo(privilegesList);
    }

}