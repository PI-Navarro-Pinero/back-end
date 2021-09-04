package com.pi.back.web.users;

import com.pi.back.config.security.Privileges;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private static final int USERNAME_MAX_LENGHT = 50;
    private static final int PASSWORD_MAX_LENGHT = 150;
    private static final int FULLNAME_MAX_LENGHT = 45;
    private static final int LICENSE_MAX_LENGHT = 45;
    private static final int EMAIL_MAX_LENGHT = 65;

    private Integer id;

    @NotEmpty
    @Size(max = USERNAME_MAX_LENGHT)
    private String username;

    @NotEmpty
    @Size(max = FULLNAME_MAX_LENGHT)
    private String fullname;

    @NotEmpty
    @Size(max = LICENSE_MAX_LENGHT)
    private String license;

    private List<Privileges> privileges;
}
