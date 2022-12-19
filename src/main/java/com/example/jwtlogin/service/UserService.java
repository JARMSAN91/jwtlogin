package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.UserDAO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.exception.InvalidUserException;
import com.example.jwtlogin.exception.TokenRefreshException;
import com.example.jwtlogin.exception.UserAlreadyExistException;
import com.example.jwtlogin.mapper.UserMapper;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    public GeneratedTokenDTO signin(String email, String password) {

        UserModel userModel = getUserByEmail(email);

        checkUserPasswordMatch(password, userModel.getPassword());

        return tokenService.mapToGeneratedDTO(tokenService.createToken(userModel));
    }

    void checkUserPasswordMatch(String password, String passwordonDB) {
        if(!passwordEncoder().matches(password, passwordonDB)) {
            throw new InvalidUserException();
        }
    }

    public GeneratedTokenDTO register(UserRegisterDTO userRegisterDTO) {

        if (!existUser(userRegisterDTO.getEmail()) && roleService.existRole(EnumRole.ROLE_USER)) {
            UserModel userModel = userMapper.map(userRegisterDTO);
            userModel.setPassword(passwordEncoder().encode(userRegisterDTO.getPassword()));
            userModel.setRoles(Set.of(roleService.getUserRoleModel(EnumRole.ROLE_USER)));
            userModel = userDAO.save(userModel);
            return tokenService.mapToGeneratedDTO(tokenService.createToken(userModel));
        } else {
            throw new UserAlreadyExistException();
        }
    }

    public UserModel getUserByEmail(String email) {

        return userDAO.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email '" + email + "' not found"));
    }

    public UserDataDTO requestUserInfo(HttpServletRequest request) {

        return userMapper.map(findUserByToken(request));
    }

    public UserModel findUserByToken (HttpServletRequest request) {

        tokenService.validateTokenWithRequest(request);
        return userDAO
                .findOneByEmail(tokenService.getEmail(tokenService.resolveToken(request)))
                .orElseThrow(() -> new UsernameNotFoundException("Email with jwtToken not found"));
    }

    public Boolean existUser(String email) {

        return userDAO.existsByEmail(email);
    }

    public AccessTokenDTO refreshUserToken(String refreshTokenDTO) {

        return tokenService.findByToken(refreshTokenDTO)
                .map(tokenService::verifyExpiration)
                .map(tokenService::refreshUserAccessToken)
                .map(tokenService::saveTokenModel)
                .map(tokenService::mapToAccessTokenDTO).orElseThrow(TokenRefreshException::new);

    }

    public void deleteToken(HttpServletRequest request) {

        tokenService.deleteTokenByUser(findUserByToken(request));
    }


}
