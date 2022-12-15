package com.example.jwtlogin.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(apiInfo().version("3.0.1"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Login with JSON Web Token Authentication API")
                .description("Simple jwt login example")
                .version("0.0.1")
                .contact(apiContact());
    }



    private Contact apiContact() {
        return new Contact()
                .name("Julio Alberto Raposo")
                .email("julioalberto91@gmail.com");
    }

}