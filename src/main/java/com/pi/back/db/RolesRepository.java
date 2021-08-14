package com.pi.back.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Role,Integer> {

    @Query(value =  "SELECT R FROM Role R WHERE R.id = :roleId")
    Role findRole(int roleId);
}
