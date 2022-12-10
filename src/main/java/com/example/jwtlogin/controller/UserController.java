package com.example.jwtlogin.controller;

import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.UserLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value= "/v2/auth")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<GeneratedTokenDTO> login(@RequestBody UserLoginDTO userLoginDto) {
        log.info("User data is email {} and password {}", userLoginDto.getEmail(), userLoginDto.getPassword());
        return ResponseEntity.ok(new GeneratedTokenDTO("token"));
    }
}
