package com.pi.back.web.users;

import com.pi.back.config.security.Privileges;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private static final int USERNAME_MAX_LENGHT = 50;
    private static final int FULLNAME_MAX_LENGHT = 45;
    private static final int LICENSE_MAX_LENGHT = 45;

    @NotBlank(message = "username must be provided")
    @Size(max = USERNAME_MAX_LENGHT)
    private String username;

    @NotBlank(message = "name must be provided")
    @Size(max = FULLNAME_MAX_LENGHT)
    private String fullname;

    @NotBlank(message = "license must be provided")
    @Size(max = LICENSE_MAX_LENGHT)
    private String license;

    @Schema(implementation = Privileges.Values.class, example = "[ ROLE_ADMIN, ROLE_AGENT ]")
    private List<Privileges> privileges;
}
