package com.example.jwtlogin.mapper;

import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.model.RefreshTokenModel;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenMapper {

    public GeneratedTokenDTO map(RefreshTokenModel refreshTokenModel) {

        final GeneratedTokenDTO generatedTokenDTO = new GeneratedTokenDTO();

        generatedTokenDTO.setRefreshToken(refreshTokenModel.getRefreshToken());
        generatedTokenDTO.setAccessToken(refreshTokenModel.getAccessToken());

        return generatedTokenDTO;
    }

    public AccessTokenDTO mapToAccessToken(RefreshTokenModel refreshTokenModel) {

        final AccessTokenDTO acessTokenDTO = new AccessTokenDTO();

        acessTokenDTO.setAccessToken(refreshTokenModel.getAccessToken());

        return  acessTokenDTO;
    }
}
