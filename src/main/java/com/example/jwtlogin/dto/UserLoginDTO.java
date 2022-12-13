package com.example.jwtlogin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginDTO {

    @NotBlank(message="Cannot be empty")
    private String email;
    @NotBlank(message="Cannot be empty")
    private String password;
}
