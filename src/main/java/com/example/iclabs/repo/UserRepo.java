package com.example.iclabs.repo;

import com.example.iclabs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    public Optional<User> findByNim(String email);

    

    @Modifying
    @Transactional
    @Query("update User u set u.name = :name where u.nim = :nim")
    public void updateUser(@Param("name") String name, @Param("nim") String nim);

    @Query("select u.name, u.nim, u.nameMateri, u.image, u.cv from User u")
    public List<User> getAllData();

}
