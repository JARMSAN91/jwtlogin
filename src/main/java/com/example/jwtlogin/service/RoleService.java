package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.UserRoleDAO;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserRoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private UserRoleDAO userRoleDAO;

    public void createRoleStandart() {

        UserRoleModel userRoleModelUser = new UserRoleModel();
        userRoleModelUser.setName(EnumRole.ROLE_USER);
        UserRoleModel userRoleModelModerator = new UserRoleModel();
        userRoleModelModerator.setName(EnumRole.ROLE_MODERATOR);
        UserRoleModel userRoleModelAdmin = new UserRoleModel();
        userRoleModelAdmin.setName(EnumRole.ROLE_ADMIN);
        userRoleDAO.save(userRoleModelUser);
        userRoleDAO.save(userRoleModelModerator);
        userRoleDAO.save(userRoleModelAdmin);
    }


    public boolean existRole(EnumRole roleUser) {

        return userRoleDAO.existsByName(roleUser);
    }

    public UserRoleModel getUserRoleModel(EnumRole roleUser) {

        return userRoleDAO.findByName(roleUser).orElseThrow( );
    }
}
