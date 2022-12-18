package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.UserRoleDAO;
import com.example.jwtlogin.exception.InvalidUserRoleException;
import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserRoleModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TestRoleService {

    private static final UserRoleModel USER_ROLE_MODEL = new UserRoleModel();
    private static final EnumRole ENUM_ROLE_USER = EnumRole.ROLE_USER;

    @InjectMocks
    private RoleService roleService;
    @Mock
    private UserRoleDAO userRoleDAO;


    @Test
    public void existRoleOK() {

        USER_ROLE_MODEL.setName(ENUM_ROLE_USER);
        Mockito.when((userRoleDAO).existsByName(ENUM_ROLE_USER)).thenReturn(true);
        boolean userRoleExist = roleService.existRole(ENUM_ROLE_USER);
        Assertions.assertTrue(userRoleExist);
    }
    @Test
    public void existRoleKO() {
        boolean userRoleExist = roleService.existRole(null);
        Assertions.assertFalse(userRoleExist);
    }

    @Test
    public void getUserRoleModelOK() {

        USER_ROLE_MODEL.setName(ENUM_ROLE_USER);
        Mockito.when((userRoleDAO).findByName(ENUM_ROLE_USER)).thenReturn(Optional.of(USER_ROLE_MODEL));
        UserRoleModel userRoleModel = roleService.getUserRoleModel(ENUM_ROLE_USER);
        Assertions.assertNotNull(userRoleModel);
        Assertions.assertEquals(userRoleModel.getName().name(), ENUM_ROLE_USER.name());
    }
    @Test
    public void getUserRoleModelKO() {

        Assertions.assertThrowsExactly(InvalidUserRoleException.class, () -> roleService.getUserRoleModel(null));
    }
}
