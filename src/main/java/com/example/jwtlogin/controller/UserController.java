package com.example.jwtlogin.controller;

import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v2/auth")
public class UserController {

    @PostMapping("/login")
    @Operation(
            summary = "${UserController.login}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GeneratedTokenDTO.class)))})
    public ResponseEntity<GeneratedTokenDTO> login(@org.springframework.web.bind.annotation.RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody UserLoginDTO userLoginDto) {
        log.info("User data is email {} and password {}", userLoginDto.getEmail(), userLoginDto.getPassword());
        return ResponseEntity.ok(new GeneratedTokenDTO("token"));
    }


    @GetMapping("/me")
    @Operation(
            summary = "${UserController.me}",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = UserDataDTO.class)))})
    public ResponseEntity<UserDataDTO> requesUserInfo() {

        return ResponseEntity.ok(new UserDataDTO("1", "asdsad@asfa.com"));
    }
}
