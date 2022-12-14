package com.example.jwtlogin.controller;

import com.example.jwtlogin.dto.MessageDTO;
import com.example.jwtlogin.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ControllerAdvice(basePackages = "com.example.jwtlogin.controller.v2")
public class V2ControllerAdvice {

    @Value("${exception.error.credentials}")
    private String ERROR_CREDENTIALS;
    @Value("${exception.error.token.expired}")
    private String ERROR_TOKEN_EXPIRED;
    @Value("${exception.error.signature}")
    private String ERROR_SIGNATURE;
    @Value("${exception.error.user.already.exist}")
    private String ERROR_USER_ALREADY_EXISTS;
    @Value("${exception.error.user.not.found}")
    private String ERROR_USER_NOT_FOUND;
    @Value("${exception.error.jwt.empty}")
    private String ERROR_JWT_EMPTY;
    @Value("${exception.error.jwt.refresh.expired}")
    private String ERROR_JWT_REFRESH_EXPIRED;
    @Value("${exception.error.jwt.refresh.exception}")
    private String ERROR_JWT_REFRESH_EXCEPTION;
    @Value("${exception.error.user.role.exception}")
    private String USER_ROLE_EXCEPTION;

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidUserException.class)
    public MessageDTO invalidUserException(InvalidUserException e) {
        return new MessageDTO(ERROR_CREDENTIALS);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ExpiredJwtException.class)
    public MessageDTO tokenExpiredException(ExpiredJwtException e) {
        return new MessageDTO(ERROR_TOKEN_EXPIRED);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public MessageDTO signatureException(SignatureException e) {
        return new MessageDTO(ERROR_SIGNATURE);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidRequestException.class)
    public MessageDTO signatureException(InvalidRequestException e) {
        return new MessageDTO(ERROR_SIGNATURE);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(UserAlreadyExistException.class)
    public MessageDTO userAlreadyExistException(UserAlreadyExistException e) {
        return new MessageDTO(ERROR_USER_ALREADY_EXISTS);
    }
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(UsernameNotFoundException.class)
    public MessageDTO usernameNotFoundException(UsernameNotFoundException e) {
        return new MessageDTO(ERROR_USER_NOT_FOUND);
    }
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IllegalArgumentException.class)
    public MessageDTO jwtStringArgumentCannotBenullOrEmpty(IllegalArgumentException e) {
        return new MessageDTO(ERROR_JWT_EMPTY);
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(TokenRefreshExpirationException.class)
    public MessageDTO jwtRefreshTokenIsExpired(TokenRefreshExpirationException e) {
        return new MessageDTO(ERROR_JWT_REFRESH_EXPIRED);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(TokenRefreshException.class)
    public MessageDTO jwtRefreshException(TokenRefreshException e) {
        return new MessageDTO(ERROR_JWT_REFRESH_EXCEPTION);
    }
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidUserRoleException.class)
    public MessageDTO jwtRefreshException(InvalidUserRoleException e) {
        return new MessageDTO(USER_ROLE_EXCEPTION);
    }

}