package com.example.jwtlogin.service;

import com.example.jwtlogin.dao.RefreshTokenDAO;
import com.example.jwtlogin.dto.GeneratedTokenDTO;
import com.example.jwtlogin.dto.AccessTokenDTO;
import com.example.jwtlogin.exception.TokenRefreshExpirationException;
import com.example.jwtlogin.mapper.RefreshTokenMapper;
import com.example.jwtlogin.model.RefreshTokenModel;
import com.example.jwtlogin.model.UserModel;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {

  @Value("${token.secret-key:secret-key}")
  private String secretKey;
  @Value("${accesstoken.expire-length}")
  private Long validityInMilliseconds;
  @Value("${refreshtoken.expire-length}")
  private Long refreshTokenDurationMs;
  @Autowired
  private RefreshTokenDAO refreshTokenDAO;
  @Autowired
  private RefreshTokenMapper refreshTokenMapper;

  public RefreshTokenModel createToken(UserModel userModel) {

    RefreshTokenModel refreshToken = new RefreshTokenModel();
    Optional<RefreshTokenModel> optionalRefreshTokenModel = refreshTokenDAO.findOneByUser(userModel);

    if(optionalRefreshTokenModel.isPresent()){
      refreshToken = optionalRefreshTokenModel.get();
    } else {
      refreshToken.setUser(userModel);
      refreshToken.setRefreshToken(createRefreshToken());
    }

    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setAccessToken(createAccessToken(createClaimsByUser(userModel)));
    return saveTokenModel(refreshToken);
  }

  private Claims createClaimsByUser(UserModel userModel) {

    Claims claims = Jwts.claims().setSubject(userModel.getEmail());
    claims.put("auth", userModel.getRoles().stream().map(userRole -> new SimpleGrantedAuthority(userRole.getName().name())).collect(Collectors.toList()));

    return claims;
  }

  private String createAccessToken(Claims claims) {

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)//
            .compact();
  }

  public String createRefreshToken() {

    return UUID.randomUUID().toString();
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
    Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // TODO refactor with assigned Key
    return true;
  }

  public Boolean validateTokenWithRequest(HttpServletRequest request) {

    return validateToken(resolveToken(request));
  }

  public Optional<RefreshTokenModel> findByToken(String refreshToken) {
    return refreshTokenDAO.findByRefreshToken(refreshToken);
  }

  public RefreshTokenModel verifyExpiration(RefreshTokenModel refreshTokenModel) {
    if (refreshTokenModel.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenDAO.delete(refreshTokenModel);
      throw new TokenRefreshExpirationException();
    }

    return refreshTokenModel;
  }

  public RefreshTokenModel refreshUserAccessToken(RefreshTokenModel refreshTokenModel) {

    refreshTokenModel.setAccessToken(createAccessToken(createClaimsByUser(refreshTokenModel.getUser())));

    return refreshTokenModel;
  }
  public RefreshTokenModel saveTokenModel(RefreshTokenModel refreshTokenModel) {

    return refreshTokenDAO.save(refreshTokenModel);
  }

  public GeneratedTokenDTO mapToGeneratedDTO(RefreshTokenModel token) {

    return refreshTokenMapper.map(token);
  }

  public AccessTokenDTO mapToAccessTokenDTO(RefreshTokenModel refreshTokenModel) {

    return refreshTokenMapper.mapToAccessToken(refreshTokenModel);
  }

  public void deleteTokenByUser(UserModel userModel) {

    refreshTokenDAO.deleteByUser(userModel);
  }
}