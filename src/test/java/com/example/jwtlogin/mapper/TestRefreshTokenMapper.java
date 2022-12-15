package com.example.jwtlogin.mapper;

import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.model.RefreshTokenModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TestRefreshTokenMapper {


    RefreshTokenMapper refreshTokenMapper = new RefreshTokenMapper();

    @Test
    public void testMap() {

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();
        refreshTokenModel.setRefreshToken("refreshToken");
        refreshTokenModel.setAccessToken("accessToken");
        final GeneratedTokenDTO token = refreshTokenMapper.map(refreshTokenModel);

        Assertions.assertEquals("refreshToken", token.getRefreshToken());
        Assertions.assertEquals("accessToken", token.getAccessToken());
    }

    @Test
    public void testMapToAccessToken() {

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();
        refreshTokenModel.setRefreshToken("refreshToken");
        refreshTokenModel.setAccessToken("accessToken");
        final AccessTokenDTO accessToken = refreshTokenMapper.mapToAccessToken(refreshTokenModel);

        Assertions.assertEquals("accessToken", accessToken.getAccessToken());
    }
}
