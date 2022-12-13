package com.example.jwtlogin.dao;

import com.example.jwtlogin.model.RefreshTokenModel;
import com.example.jwtlogin.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenDAO extends JpaRepository<RefreshTokenModel, Long> {

    Optional<RefreshTokenModel> findByAccessToken(String accessToken);

    Optional<RefreshTokenModel> findByRefreshToken(String refreshToken);

    @Modifying
    int deleteByUser(UserModel userModel);
}
