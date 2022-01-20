package com.pi.back.users;

import com.pi.back.config.security.Privileges;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "USER")
@Entity
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "USERNAME", unique = true)
    private String username;

    @Column(nullable = false, name = "PASSWORD")
    private String password;

    @Column(nullable = false, name = "FULLNAME")
    private String fullname;

    @Column(nullable = false, name = "LICENSE")
    private String license;

    @Column(nullable = false, name = "ROLE_ADMIN")
    private boolean roleAdmin;

    @Column(nullable = false, name = "ROLE_AGENT")
    private boolean roleAgent;

    @ElementCollection(targetClass = Privileges.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Privileges> roles = Collections.emptyList();

    public List<Privileges> getRoles() {
        List<Privileges> roles = new ArrayList<>();

        if (roleAdmin) roles.add(Privileges.ROLE_ADMIN);
        if (roleAgent) roles.add(Privileges.ROLE_AGENT);

        return roles;
    }

    public static class UserBuilder {
        public UserBuilder roles(List<Privileges> roles) {
            for (Privileges role : roles) {
                if (role.equals(Privileges.ROLE_ADMIN)) roleAdmin = true;
                else if (role.equals(Privileges.ROLE_AGENT)) roleAgent = true;
            }

            return this;
        }
    }
}
