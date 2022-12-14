package com.example.jwtlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedTokenDTO {

    private String accessToken;
    private String refreshToken;
}
