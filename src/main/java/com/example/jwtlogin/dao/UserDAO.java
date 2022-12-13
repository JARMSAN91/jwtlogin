package com.example.jwtlogin.dao;

import com.example.jwtlogin.model.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDAO extends JpaRepository<UserModel, Integer> {

    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findOneByEmailAndPassword(String email, String password);

    @Transactional
    void deleteByEmail(String email);
}
