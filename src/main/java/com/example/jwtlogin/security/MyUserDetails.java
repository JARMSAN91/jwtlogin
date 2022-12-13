package com.example.jwtlogin.security;

import com.example.jwtlogin.dao.UserDAO;
import com.example.jwtlogin.model.UserModel;
import com.example.jwtlogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetails implements UserDetailsService {

  @Autowired
  private UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final UserModel userModel = userService.getUserByEmail(username);

    if (userModel == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    return User
        .withUsername(username)
        .password(userModel.getPassword())
        .authorities(buildAuthority(userModel))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

  public List<GrantedAuthority> buildAuthority(UserModel userModel) {
    return userModel.getRoles().stream()
            .map(userRole -> new SimpleGrantedAuthority(userRole.getName().name()))
            .collect(Collectors.toList());
  }

}
