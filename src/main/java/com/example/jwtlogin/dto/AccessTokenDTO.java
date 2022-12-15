package com.example.jwtlogin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenDTO {

    @NotBlank(message="Cannot be empty")
    private String accessToken;
}
