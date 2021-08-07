package com.pi.back.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "USERS")
@Entity
public class Users {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false, name = "USERNAME", unique = true)
    private String username;

    @Column(nullable = false, name = "PASSWORD")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLEID")
    private Roles roleId;
}