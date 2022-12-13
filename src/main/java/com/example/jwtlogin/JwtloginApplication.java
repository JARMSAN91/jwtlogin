package com.example.jwtlogin;

import com.example.jwtlogin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class JwtloginApplication implements CommandLineRunner {

	@Autowired
	RoleService roleService;

	public static void main(String[] args) {
		SpringApplication.run(JwtloginApplication.class, args);
	}

	@Override
	public void run(String... params) throws Exception {

		roleService.createRoleStandart();
	}
}
