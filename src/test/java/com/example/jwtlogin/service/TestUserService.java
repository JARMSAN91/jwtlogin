package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.UserDAO;
import com.example.jwtlogin.dao.UserRoleDAO;
import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.exception.InvalidUserException;
import com.example.jwtlogin.exception.InvalidUserRoleException;
import com.example.jwtlogin.exception.TokenRefreshException;
import com.example.jwtlogin.exception.UserAlreadyExistException;
import com.example.jwtlogin.mapper.UserMapper;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.RefreshTokenModel;
import com.example.jwtlogin.model.UserModel;
import com.example.jwtlogin.model.UserRoleModel;
import jakarta.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TestUserService {

    private static final UserModel USER_MODEL = new UserModel();
    private static final UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO();
    private static final AccessTokenDTO ACCESS_TOKEN_DTO = new AccessTokenDTO();
    private static final String USER_EMAIL_OK = "prueba@prueba.com";
    private static final String USER_EMAIL_KO = "ko@prueba.com";
    private static final String PASS = "123456890";
    private static final String PHONE = "123456890";
    private static final String REFRESH_TOKEN_OK = "bf296257-001f-43ed-b9f3-9bda84b9faaf";
    private static final String ACCESS_TOKEN_OK = """
            eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcnVlYmFAcHJ1ZWJhLmNvbSIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiaW
            F0IjoxNjcxNDY3MTY1LCJleHAiOjE2NzE0Njc0NjV9.fX_Vcx6GgsArGNTLWk_Y_bNMHgWIR7dP5zLUKLot-QM""";

    @InjectMocks
    private UserService userService;
    @Mock
    private UserDAO userDAO;
    @Mock
    private TokenService tokenService;
    @Mock
    private RoleService  roleService;
    @Mock
    private UserMapper userMapper;


    @Test
    public void getUserByEmailOK() {

        USER_MODEL.setEmail(USER_EMAIL_OK);
        USER_MODEL.setPassword(PASS);
        Mockito.when(userDAO.findOneByEmail(Mockito.anyString())).thenReturn(Optional.of(USER_MODEL));

        Assertions.assertEquals(userService.getUserByEmail(USER_EMAIL_OK).getEmail(), USER_EMAIL_OK);

    }
    @Test
    public void getUserByEmailKO() {

        Assertions.assertThrowsExactly(UsernameNotFoundException.class, () -> userService.getUserByEmail(null));
    }

    @Test
    public void existUserOK() {

        Mockito.when(userDAO.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertEquals(true, userService.existUser(USER_EMAIL_OK));
    }
    @Test
    public void existUserKO() {

        Mockito.when(userDAO.existsByEmail(Mockito.anyString())).thenReturn(false);

        Assertions.assertEquals(false, userService.existUser(USER_EMAIL_KO));
    }

    @Test
    public void refreshUserTokenOK(){

        ACCESS_TOKEN_DTO.setAccessToken(ACCESS_TOKEN_OK);
        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();

        Mockito.when(tokenService.findByToken(Mockito.anyString())).thenReturn(Optional.of(refreshTokenModel));
        Mockito.when(tokenService.verifyExpiration(Mockito.any())).thenReturn(refreshTokenModel);
        Mockito.when(tokenService.refreshUserAccessToken(Mockito.any())).thenReturn(refreshTokenModel);
        Mockito.when(tokenService.saveTokenModel(Mockito.any())).thenReturn(refreshTokenModel);
        Mockito.when(tokenService.mapToAccessTokenDTO(Mockito.any())).thenReturn(ACCESS_TOKEN_DTO);

        Assertions.assertEquals(ACCESS_TOKEN_OK, (userService.refreshUserToken(REFRESH_TOKEN_OK).getAccessToken()));
    }
    @Test
    public void refreshUserTokenKO(){

        Assertions.assertThrowsExactly(TokenRefreshException.class, () -> userService.refreshUserToken(null));
    }

    @Test
    public void findUserByTokenOK() {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer ".concat(ACCESS_TOKEN_OK));
        USER_MODEL.setEmail(USER_EMAIL_OK);
        USER_MODEL.setPassword(PASS);

        Mockito.when(tokenService.validateTokenWithRequest(Mockito.any())).thenReturn(true);
        Mockito.when(userDAO.findOneByEmail(Mockito.anyString())).thenReturn(Optional.of(USER_MODEL));

        Assertions.assertEquals(Boolean.TRUE, tokenService.validateTokenWithRequest(request));
        Assertions.assertEquals(USER_EMAIL_OK, userDAO.findOneByEmail(USER_EMAIL_OK).get().getEmail());
    }

    @Test
    public void findUserByTokenKO() {

        Assertions.assertThrowsExactly(UsernameNotFoundException.class, () -> userService.findUserByToken(null));
    }


    @Test
    public void requestUserInfoKO() {

        Assertions.assertThrowsExactly(UsernameNotFoundException.class, () -> userService.requestUserInfo(null));
    }

    @Test
    public void registerOK() {

        USER_REGISTER_DTO.setName("name");
        USER_REGISTER_DTO.setEmail(USER_EMAIL_OK);
        USER_REGISTER_DTO.setPassword(PASS);
        USER_REGISTER_DTO.setPhone(PHONE);

        Mockito.when(roleService.existRole(Mockito.any())).thenReturn(Boolean.TRUE);
        Mockito.when(userMapper.map(Mockito.any(UserRegisterDTO.class))).thenReturn(new UserModel());
        Mockito.when(roleService.getUserRoleModel(EnumRole.ROLE_USER)).thenReturn(new UserRoleModel());
        Mockito.when(userService.register(USER_REGISTER_DTO)).thenReturn(new GeneratedTokenDTO(
                ACCESS_TOKEN_OK, REFRESH_TOKEN_OK
        ));

        GeneratedTokenDTO generatedTokenDTO = userService.register(USER_REGISTER_DTO);

        Assertions.assertNotNull(generatedTokenDTO);
        Assertions.assertEquals(ACCESS_TOKEN_OK, generatedTokenDTO.getAccessToken());
        Assertions.assertEquals(REFRESH_TOKEN_OK, generatedTokenDTO.getRefreshToken());
    }

    @Test
    public void registerKO() {

        USER_REGISTER_DTO.setName("name");
        USER_REGISTER_DTO.setEmail(USER_EMAIL_OK);
        USER_REGISTER_DTO.setPassword(PASS);
        USER_REGISTER_DTO.setPhone(PHONE);

        Assertions.assertThrowsExactly(UserAlreadyExistException.class, () -> userService.register(USER_REGISTER_DTO));
    }

    @Test
    public void signinKO() {

        Mockito.when(userDAO.findOneByEmail(Mockito.anyString())).thenReturn(Optional.of(USER_MODEL));
        Assertions.assertThrowsExactly(InvalidUserException.class, () -> userService.signin(USER_EMAIL_OK,"prueba"));
    }

}
