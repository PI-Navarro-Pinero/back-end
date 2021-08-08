package com.pi.back.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Integer> {

    @Query(value =  "SELECT U FROM Users U WHERE U.id = :userId")
    Users findUser(int userId);
}
