package com.example.jwtlogin.mapper;

import com.example.jwtlogin.dto.UserDataDTO;
import com.example.jwtlogin.dto.UserRegisterDTO;
import com.example.jwtlogin.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserModel map(UserRegisterDTO userRegisterDTO){

        UserModel userModel = new UserModel();

        userModel.setEmail(userRegisterDTO.getEmail());
        userModel.setName(userRegisterDTO.getName());
        userModel.setPhone(userRegisterDTO.getPhone());

        return userModel;
    }

    public UserDataDTO map(UserModel userModel) {

        UserDataDTO userDataDTO = new UserDataDTO();

        userDataDTO.setEmail(userModel.getEmail());
        userDataDTO.setId(userModel.getId());

        return userDataDTO;
    }
}
