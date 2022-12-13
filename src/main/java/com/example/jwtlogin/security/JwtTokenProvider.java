package com.example.jwtlogin.security;

import com.example.jwtlogin.dao.RefreshTokenDAO;
import com.example.jwtlogin.exception.CustomException;
import com.example.jwtlogin.model.RefreshTokenModel;
import com.example.jwtlogin.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {

  @Value("${token.secret-key:secret-key}")
  private String secretKey;

  @Value("${accesstoken.expire-length}")
  private Long validityInMilliseconds;

  @Value("${refreshtoken.expire-length}")
  private Long refreshTokenDurationMs;

  @Autowired
  private RefreshTokenDAO refreshTokenDAO;

  public RefreshTokenModel createToken(UserModel userModel) {

    Claims claims = Jwts.claims().setSubject(userModel.getEmail());
    claims.put("auth", userModel.getRoles().stream().map(userRole -> new SimpleGrantedAuthority(userRole.getName().name())).collect(Collectors.toList()));
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    RefreshTokenModel refreshToken = new RefreshTokenModel();
    refreshToken.setAccessToken(Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)//
            .compact());

    return createRefreshToken(userModel, refreshToken);
  }

  public String getEmail(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject(); //  TODO refactor with assigned Key
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // TODO refactor with assigned Key
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public RefreshTokenModel createRefreshToken(UserModel userModel, RefreshTokenModel refreshToken) {

    refreshToken.setUser(userModel);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setRefreshToken(UUID.randomUUID().toString());

    refreshToken = refreshTokenDAO.save(refreshToken);
    return refreshToken;
  }

}
