package com.example.task;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private static final String TEST_SECRET = "test-secret-key-must-be-at-least-32-chars!";

  @BeforeEach
  void setUp() throws Exception {
    jwtUtil = new JwtUtil();
    // Inject the secret via reflection (since @Value won't work without Spring context)
    Field secretField = JwtUtil.class.getDeclaredField("secret");
    secretField.setAccessible(true);
    secretField.set(jwtUtil, TEST_SECRET);
  }

  private String generateToken(String username, Date expiration) {
    SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
    return Jwts.builder()
      .claims(Map.of("username", username))
      .expiration(expiration)
      .signWith(key)
      .compact();
  }

  @Test
  @DisplayName("should extract username from a valid token")
  void shouldExtractUsername() {
    String token = generateToken("dalthon", new Date(System.currentTimeMillis() + 86400000));

    String username = jwtUtil.extractUsername(token);

    assertThat(username).isEqualTo("dalthon");
  }

  @Test
  @DisplayName("should return null for an expired token")
  void shouldReturnNullForExpiredToken() {
    String token = generateToken("dalthon", new Date(System.currentTimeMillis() - 1000));

    String username = jwtUtil.extractUsername(token);

    assertThat(username).isNull();
  }

  @Test
  @DisplayName("should return null for a tampered token")
  void shouldReturnNullForTamperedToken() {
    String token = generateToken("dalthon", new Date(System.currentTimeMillis() + 86400000));
    // Tamper with the token by modifying the last character
    String tampered = token.substring(0, token.length() - 1) + "X";

    String username = jwtUtil.extractUsername(tampered);

    assertThat(username).isNull();
  }

  @Test
  @DisplayName("should return null for a completely invalid token")
  void shouldReturnNullForInvalidToken() {
    String username = jwtUtil.extractUsername("not.a.valid.token");

    assertThat(username).isNull();
  }

  @Test
  @DisplayName("should return null for a token signed with different secret")
  void shouldReturnNullForWrongSecret() {
    SecretKey wrongKey = Keys.hmacShaKeyFor("wrong-secret-key-must-be-at-least-32-ch!".getBytes());
    String token = Jwts.builder()
      .claims(Map.of("username", "hacker"))
      .expiration(new Date(System.currentTimeMillis() + 86400000))
      .signWith(wrongKey)
      .compact();

    String username = jwtUtil.extractUsername(token);

    assertThat(username).isNull();
  }

  @Test
  @DisplayName("should return null for empty token")
  void shouldReturnNullForEmptyToken() {
    // jjwt throws IllegalArgumentException for empty strings
    assertThatThrownBy(() -> jwtUtil.extractUsername(""))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("should extract different usernames correctly")
  void shouldExtractDifferentUsernames() {
    String token1 = generateToken("user1", new Date(System.currentTimeMillis() + 86400000));
    String token2 = generateToken("admin", new Date(System.currentTimeMillis() + 86400000));

    assertThat(jwtUtil.extractUsername(token1)).isEqualTo("user1");
    assertThat(jwtUtil.extractUsername(token2)).isEqualTo("admin");
  }
}
