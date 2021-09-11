package com.pi.back.services;

import com.pi.back.db.Role;
import com.pi.back.db.RolesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private RolesService sut;

    @Test
    void findAllTest() {
        Role role = Role.builder().build();

        when(rolesRepository.findAll()).thenReturn(List.of(role));

        List<Role> actual = sut.findAll();

        assertThat(actual).isEqualTo(List.of(role));
    }

}