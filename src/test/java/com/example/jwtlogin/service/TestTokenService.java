package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.RefreshTokenDAO;
import com.example.jwtlogin.exception.InvalidUserException;
import com.example.jwtlogin.exception.TokenRefreshException;
import com.example.jwtlogin.exception.TokenRefreshExpirationException;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.RefreshTokenModel;
import com.example.jwtlogin.model.UserModel;
import com.example.jwtlogin.model.UserRoleModel;
import io.jsonwebtoken.security.Keys;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"refreshtoken.expire-length=1000L", "accesstoken.expire-length=5000L"})
public class TestTokenService {

    private static final String EMAIL = "test@test.com";
    private static final String PASS = "pass";
    private static final String REFRESH_TOKEN_OK = "bf296257-001f-43ed-b9f3-9bda84b9faaf";
    private static final String ACCESS_TOKEN_OK = """
            eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcnVlYmFAcHJ1ZWJhLmNvbSIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiaW
            F0IjoxNjcxNDY3MTY1LCJleHAiOjE2NzE0Njc0NjV9.fX_Vcx6GgsArGNTLWk_Y_bNMHgWIR7dP5zLUKLot-QM""";

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RefreshTokenDAO refreshTokenDAO;

    @Test
    public void refreshTokenDAO() {

        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Assertions.assertTrue(UUID_REGEX.matcher(tokenService.createRefreshToken()).matches());
    }

    @Test
    public void createToken() throws InvalidUserException {


        UserRoleModel userRoleModel= new UserRoleModel();
        userRoleModel.setName(EnumRole.ROLE_USER);
        UserModel userModel = new UserModel();
        userModel.setEmail(EMAIL);
        userModel.setRoles(Set.of(userRoleModel));
        userModel.setPassword(PASS);

        ReflectionTestUtils.setField(tokenService, "refreshTokenDurationMs", 5000L);
        ReflectionTestUtils.setField(tokenService, "validityInMilliseconds", 5000L);
        ReflectionTestUtils.setField(tokenService, "secretKey",
                "zdWIiOiJwcnVlYmFAcHJ1ZWJhLmNvbSIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiaW");

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();
        refreshTokenModel.setAccessToken(ACCESS_TOKEN_OK);
        refreshTokenModel.setRefreshToken(REFRESH_TOKEN_OK);

        Mockito.when(refreshTokenDAO.save(Mockito.any())).thenReturn(refreshTokenModel);

        refreshTokenModel = tokenService.createToken(userModel);

        Assertions.assertTrue(refreshTokenModel.getRefreshToken().matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));

    }
    @Test
    public void verifyExpirationOK() {

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();
        refreshTokenModel.setExpiryDate(Instant.now().plusMillis(1000));
        refreshTokenModel.setRefreshToken(REFRESH_TOKEN_OK);
        refreshTokenModel.setAccessToken(ACCESS_TOKEN_OK);

        refreshTokenModel = tokenService.verifyExpiration(refreshTokenModel);

        Assertions.assertNotNull(refreshTokenModel);
        Assertions.assertEquals(refreshTokenModel.getRefreshToken(), REFRESH_TOKEN_OK);
        Assertions.assertEquals(refreshTokenModel.getAccessToken(), ACCESS_TOKEN_OK);
    }
    @Test
    public void verifyExpirationKO() {

        RefreshTokenModel refreshTokenModel = new RefreshTokenModel();
        refreshTokenModel.setExpiryDate(Instant.now().minusMillis(1000));

        Assertions.assertThrowsExactly(TokenRefreshExpirationException.class, () -> tokenService.verifyExpiration(refreshTokenModel));
    }

}
