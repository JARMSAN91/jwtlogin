package com.example.jwtlogin.mapper;

import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserModel;
import org.apache.catalina.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class TestUserMapper {

    UserMapper userMapper = new UserMapper();

    @Test
    public void testMap() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("pruaba@€ba.com");
        userRegisterDTO.setName("name");
        userRegisterDTO.setPhone("phone");
        userRegisterDTO.setPassword("password");

        UserModel userModel = userMapper.map(userRegisterDTO);

        Assertions.assertEquals("pruaba@€ba.com", userModel.getEmail());
        Assertions.assertEquals("name", userModel.getName());
        Assertions.assertEquals("phone", userModel.getPhone());
    }

    @Test
    public void testMap2() {

        UserModel userModel = new UserModel();

        userModel.setEmail("prueba@prueba.com");
        userModel.setPhone("123456");
        userModel.setName("name");
        userModel.setPassword("12345");

        UserDataDTO userDataDTO = userMapper.map(userModel);

        Assertions.assertEquals("prueba@prueba.com", userDataDTO.getEmail());
        Assertions.assertInstanceOf(UserDataDTO.class, userDataDTO);
    }
}
