package com.example.jwtlogin.dao;

import com.example.jwtlogin.model.EnumRole;
import com.example.jwtlogin.model.UserModel;
import com.example.jwtlogin.model.UserRoleModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleDAO extends JpaRepository<UserRoleModel, Integer> {

    boolean existsByName(EnumRole roleUser);

    Optional<UserRoleModel> findByName(EnumRole roleUser);
}
