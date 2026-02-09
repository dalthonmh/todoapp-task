package com.example.task;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    
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
      return claims.getSubject();
    } catch (JwtException e) {
      return null;
    }
  }
}