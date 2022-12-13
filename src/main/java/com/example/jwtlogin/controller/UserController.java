package com.example.jwtlogin.controller;

import com.example.jwtlogin.dto.*;
import com.example.jwtlogin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v2/auth")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    @Operation(
            summary = "${UserController.login}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GeneratedTokenDTO.class)))})
    public ResponseEntity<GeneratedTokenDTO> login(@Valid @org.springframework.web.bind.annotation.RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody UserLoginDTO userLoginDto) {

        final GeneratedTokenDTO generatedTokenDTO = userService.signin(userLoginDto.getEmail(), userLoginDto.getPassword());
        return ResponseEntity.ok(generatedTokenDTO);
    }

    @PostMapping("/signup")
    @Operation(
            summary = "${UserController.signup}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GeneratedTokenDTO.class)))})
    public ResponseEntity<?> signUp(@Valid @org.springframework.web.bind.annotation.RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody UserRegisterDTO userRegisterDTO) {

        validUserInput(userRegisterDTO);
        return ResponseEntity.ok(userService.signup(userRegisterDTO));
    }

    private void validUserInput(UserRegisterDTO userRegisterDTO) {

        //TODO
    }

    @GetMapping("/refresh")
    @Operation(
            summary = "${UserController.refresh}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GenerateAccessTokenDTO.class)))})
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok(new GenerateAccessTokenDTO("token"));
    }

    @GetMapping("/me")
    @Operation(
            summary = "${UserController.me}",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = UserDataDTO.class)))})
    public ResponseEntity<UserDataDTO> requestUserInfo(HttpServletRequest request) {

        UserDataDTO userData = userService.findUserByToken(request);
        return ResponseEntity.ok(userData);
    }
}
