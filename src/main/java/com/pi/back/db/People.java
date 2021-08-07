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
@Table(name = "PEOPLE")
@Entity
public class People {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(nullable = false, name = "FULLNAME")
    private String fullname;

    @Column(nullable = false, name = "CUIL", unique = true)
    private String cuil;

    @Column(nullable = false, name = "EMAIL", unique = true)
    private String email;
}
