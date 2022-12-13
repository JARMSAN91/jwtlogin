package com.example.jwtlogin.mapper;

import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.model.RefreshTokenModel;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenToGeneratedTokenMapper {

    public GeneratedTokenDTO map(RefreshTokenModel refreshTokenModel) {

        final GeneratedTokenDTO generatedTokenDTO = new GeneratedTokenDTO();

        generatedTokenDTO.setRefreshToken(refreshTokenModel.getRefreshToken());
        generatedTokenDTO.setAccessToken(refreshTokenModel.getAccessToken());

        return generatedTokenDTO;
    }
}
