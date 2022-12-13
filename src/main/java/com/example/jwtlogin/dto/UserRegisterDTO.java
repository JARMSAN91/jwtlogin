package com.example.jwtlogin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterDTO {

    @NotBlank(message="Cannot be empty")
    private String email;
    @NotBlank(message="Cannot be empty")
    private String password;
    @NotBlank(message="Cannot be empty")
    private String phone;
    @NotBlank(message="Cannot be empty")
    private String name;
}
