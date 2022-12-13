package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.UserDAO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.exception.CustomException;
import com.example.jwtlogin.mapper.RefreshTokenToGeneratedTokenMapper;
import com.example.jwtlogin.mapper.UserMapper;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserModel;
import com.example.jwtlogin.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RefreshTokenToGeneratedTokenMapper refreshTokenToGeneratedTokenMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;


    public GeneratedTokenDTO signin(String email, String password){

        UserModel userModel = userDAO.findOneByEmailAndPassword(email, password).orElseThrow(() ->new UsernameNotFoundException("Email '" + email + "' not found"));
        passwordEncoder.matches(password, userModel.getPassword());

        return refreshTokenToGeneratedTokenMapper.map(jwtTokenProvider.createToken(userModel));
    }

    public GeneratedTokenDTO signup(UserRegisterDTO userRegisterDTO){

        if(!existUser(userRegisterDTO.getEmail()) && roleService.existRole(EnumRole.ROLE_USER)) {
            UserModel userModel = userMapper.map(userRegisterDTO);
            userModel.setRoles(Set.of(roleService.getUserRoleModel(EnumRole.ROLE_USER)));
            userModel = userDAO.save(userModel);
            return refreshTokenToGeneratedTokenMapper.map(jwtTokenProvider.createToken(userModel));
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public UserModel getUserByEmail(String email) {

        return userDAO.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("Email '" + email + "' not found"));
    }

    public UserDataDTO findUserByToken(HttpServletRequest request) {

        UserModel userModel = userDAO
                .findByEmail(jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(request)))
                .orElseThrow(() ->new UsernameNotFoundException("Email with jwtToken not found"));

        return userMapper.map(userModel);
    }

    public Boolean existUser(String email) {

        return userDAO.existsByEmail(email);
    }
}
