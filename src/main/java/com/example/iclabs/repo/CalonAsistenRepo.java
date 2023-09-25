package com.example.iclabs.repo;

import com.example.iclabs.entity.CalonAsisten;
import com.example.iclabs.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalonAsistenRepo  extends JpaRepository<CalonAsisten, Long> {


    Optional<CalonAsisten> findByNim(String nim);

}
