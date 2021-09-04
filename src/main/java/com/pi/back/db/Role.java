package com.pi.back.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "roleName")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ROLE")
@Entity
public class Role {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "USER_ID")
    private Integer userId;

    @Column(nullable = false, name = "ROLE")
    private String roleName;

    @Column(name = "ROLE_ID")
    private Integer roleId;

    private String description;

    public String getDescription() {
        List<Privileges> roles = Arrays.asList(Privileges.ROLE_R, Privileges.ROLE_W, Privileges.ROLE_X);
        return roles.stream()
                .filter(privileges -> privileges.getRole().equals(roleName))
                .findFirst()
                .map(Privileges::getDescription)
                .orElse("Description not available");
    }
}
