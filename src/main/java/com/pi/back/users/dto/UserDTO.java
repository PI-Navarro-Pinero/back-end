package com.pi.back.users.dto;

import com.pi.back.config.security.Privileges;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserDTO {
    private Integer id;
    private String username;
    private String fullname;
    private String license;
    private List<Privileges> privileges;
}
