package com.example.jwtlogin.controller.v2;

import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.exception.TokenRefreshException;
import com.example.jwtlogin.exception.TokenRefreshExpirationException;
import com.example.jwtlogin.exception.UserAlreadyExistException;
import com.example.jwtlogin.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TestUserController {

    private static final String JSON_KO = "{\"email\":\"KO\",\"password\":\"KO\"}";
    private static final String JSON_SIGNUP = "{\"email\":\"KO\",\"password\":\"KO\",\"phone\":\"123\",\"name\":\"john\"}";

    @InjectMocks
    private UserController controller;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(userService).isNotNull();
    }

    @Test
    public void testTokenKoInvalidUserException() throws Exception {

        Mockito.when(userService.signin("KO", "KO")).thenThrow(UsernameNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/v2/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(JSON_KO)).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void testTokenKoExpiredJwtException() throws Exception {
        Mockito.when(userService.requestUserInfo(Mockito.any())).thenThrow(ExpiredJwtException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/auth/me")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testTokenKoSignatureException() throws Exception {
        Mockito.when(userService.requestUserInfo(Mockito.any())).thenThrow(SignatureException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/auth/me")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testKOUserAlreadyExistException() throws Exception {
        Mockito.when(userService.register(Mockito.any())).thenThrow(new UserAlreadyExistException());
        mockMvc.perform(MockMvcRequestBuilders.post("/v2/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_SIGNUP))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
    @Test
    public void testKOUsernameNotFoundException() throws Exception {
        Mockito.when(userService.requestUserInfo(Mockito.any())).thenThrow(new UsernameNotFoundException(""));
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/auth/me")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
    @Test
    public void testKOTokenRefreshExpirationException() throws Exception {
        Mockito.when(userService.refreshUserToken(Mockito.any())).thenThrow(new TokenRefreshExpirationException());
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/auth/refresh").param("refreshToken","123")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
    @Test
    public void testKOTokenRefreshException() throws Exception {
        Mockito.when(userService.refreshUserToken(Mockito.any())).thenThrow(new TokenRefreshException());
        mockMvc.perform(MockMvcRequestBuilders.get("/v2/auth/refresh").param("refreshToken","123")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void testOKLogout() throws Exception {
        Mockito.doNothing().when(userService).deleteToken(new MockHttpServletRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/v2/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    public void testOkSignin() throws Exception {
        Mockito.when(userService.signin(Mockito.any(),Mockito.any())).thenReturn(new GeneratedTokenDTO("123","1234"));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v2/auth/login").contentType(MediaType.APPLICATION_JSON).content(JSON_KO))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", org.hamcrest.core.Is.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", org.hamcrest.core.Is.is("1234")));
    }
    @Test
    public void testOkSignUp() throws Exception {
        Mockito.when(userService.register(Mockito.any())).thenReturn(new GeneratedTokenDTO("123","1234"));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v2/auth/signup").contentType(MediaType.APPLICATION_JSON).content(JSON_SIGNUP))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", org.hamcrest.core.Is.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", org.hamcrest.core.Is.is("1234")));
    }
    @Test
    public void testOkRefreshToken() throws Exception {
        Mockito.when(userService.refreshUserToken(Mockito.any())).thenReturn(new AccessTokenDTO("123"));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/v2/auth/refresh").param("refreshToken","12345"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", org.hamcrest.core.Is.is("123")));
    }
    @Test
    public void testOkMe() throws Exception {
        Mockito.when(userService.requestUserInfo(Mockito.any())).thenReturn(new UserDataDTO(1,"xx@xx.com"));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/v2/auth/me"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", org.hamcrest.core.Is.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", org.hamcrest.core.Is.is("xx@xx.com")));
    }
}
