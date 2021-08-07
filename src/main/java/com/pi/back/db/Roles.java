package com.pi.back.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ROLES")
@Entity
public class Roles {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false, name = "ROLE", unique = true)
    private String roleName;
}
