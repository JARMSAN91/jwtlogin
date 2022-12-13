package com.example.jwtlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateAccessTokenDTO {

    private String accessToken;
}
