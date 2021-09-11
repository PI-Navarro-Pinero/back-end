package com.pi.back.services;

import com.pi.back.config.security.Privileges;
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
        List<Privileges> privilegesList = List.of(Privileges.ROLE_R, Privileges.ROLE_W, Privileges.ROLE_X);

        assertThat(sut.findAll()).isEqualTo(privilegesList);
    }

}