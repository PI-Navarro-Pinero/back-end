package com.pi.back.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User,Integer> {

    @Query(value =  "SELECT U FROM User U WHERE U.id = :userId")
    User findUser(int userId);
}
