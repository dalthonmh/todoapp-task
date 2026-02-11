package com.example.task;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

  private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    
  @Value("${jwt.secret}")
  private String secret;
  
  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }
  
  // Extract username from token
  public String extractUsername(String token) {

    try {
      Claims claims = Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();

      String username = claims.get("username", String.class);

      return username;
    } catch (JwtException e) {
      log.error("JWT validation failed: {}", e.getMessage());
      return null;
    }
  }
}