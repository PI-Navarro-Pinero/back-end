package com.pi.back.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
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

    @Column(nullable = false, name = "CUIL")
    private String cuil;

    @Column(nullable = false, name = "EMAIL")
    private String email;

    @ElementCollection(targetClass = Privileges.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ROLE", joinColumns = @JoinColumn(name = "USER_ID", nullable = false))
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private List<Privileges> roles;
}
