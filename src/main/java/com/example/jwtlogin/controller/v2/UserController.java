package com.example.jwtlogin.controller.v2;

import com.example.jwtlogin.dto.*;
import com.example.jwtlogin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        return ResponseEntity.ok(userService.signin(userLoginDto.getEmail(), userLoginDto.getPassword()));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "${UserController.signup}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GeneratedTokenDTO.class)))})
    public ResponseEntity<GeneratedTokenDTO> signUp(@Valid @org.springframework.web.bind.annotation.RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody UserRegisterDTO userRegisterDTO) {

       // validUserInput(userRegisterDTO);
        return ResponseEntity.ok(userService.register(userRegisterDTO));
    }

    @GetMapping("/refresh")
    @Operation(
            summary = "${UserController.refresh}",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = GenerateAccessTokenDTO.class)))})
    public ResponseEntity<AccessTokenDTO> refresh(@Valid @Parameter(name = "refreshToken") @RequestParam  String refreshToken) {

        return ResponseEntity.ok(userService.refreshUserToken(refreshToken));
    }

    @GetMapping("/me")
    @Operation(
            summary = "${UserController.me}",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = UserDataDTO.class)))})
    public ResponseEntity<UserDataDTO> requestUserInfo(HttpServletRequest request) {

        return ResponseEntity.ok(userService.requestUserInfo(request));
    }
    @PostMapping("/logout")
    @Operation(
            summary = "${UserController.logout}",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK_MSG", content = @Content(schema = @Schema(implementation = UserDataDTO.class)))})
    public ResponseEntity<?> logout(HttpServletRequest request) {

        userService.deleteToken(request);
        return ResponseEntity.noContent().build();
    }
}
