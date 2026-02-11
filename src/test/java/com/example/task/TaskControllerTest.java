package com.example.task;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TaskRepository repository;

  @Value("${jwt.secret}")
  private String jwtSecret;

  private String validToken;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    // Generate a valid JWT token (same way as Go auth service)
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    validToken = Jwts.builder()
      .claims(Map.of("username", "testuser"))
      .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
      .signWith(key)
      .compact();
  }

  private String generateExpiredToken() {
    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.builder()
      .claims(Map.of("username", "testuser"))
      .expiration(new Date(System.currentTimeMillis() - 1000)) // already expired
      .signWith(key)
      .compact();
  }

  // ========== GET /api/tasks ==========
  @DisplayName("GET /api/tasks")
  class GetAllEndpoint {

    @Test
    @DisplayName("should return empty list when no tasks")
    void shouldReturnEmptyList() throws Exception {
      mockMvc.perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should return all tasks without auth (GET is public)")
    void shouldReturnAllTasksWithoutAuth() throws Exception {
      Task t = new Task();
      t.setTitle("Public task");
      t.setUsername("someone");
      repository.save(t);

      mockMvc.perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].title").value("Public task"));
    }
  }

  // ========== POST /api/tasks ==========
  @DisplayName("POST /api/tasks")
  class CreateEndpoint {

    @Test
    @DisplayName("should create a task with valid token")
    void shouldCreateTask() throws Exception {
      String body = """
        {"title": "New task", "completed": false}
        """;

      mockMvc.perform(post("/api/tasks")
          .header("Authorization", "Bearer " + validToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("New task"))
        .andExpect(jsonPath("$.completed").value(false))
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @DisplayName("should create task with completed=null defaulting to false")
    void shouldCreateTaskWithNullCompleted() throws Exception {
      String body = """
        {"title": "No completed field"}
        """;

      mockMvc.perform(post("/api/tasks")
          .header("Authorization", "Bearer " + validToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("No completed field"))
        .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("should return 401 when no token provided")
    void shouldReturn401WithoutToken() throws Exception {
      String body = """
        {"title": "Unauthorized task"}
        """;

      mockMvc.perform(post("/api/tasks")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return 403 when token is invalid")
    void shouldReturn403WithInvalidToken() throws Exception {
      String body = """
        {"title": "Forbidden task"}
        """;

      mockMvc.perform(post("/api/tasks")
          .header("Authorization", "Bearer invalid.token.here")
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("should return 403 when token is expired")
    void shouldReturn403WithExpiredToken() throws Exception {
      String body = """
        {"title": "Expired token task"}
        """;

      mockMvc.perform(post("/api/tasks")
          .header("Authorization", "Bearer " + generateExpiredToken())
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isForbidden());
    }
  }

  // ========== PUT /api/tasks/{id} ==========
  @DisplayName("PUT /api/tasks/{id}")
  class UpdateEndpoint {

    @Test
    @DisplayName("should update an existing task")
    void shouldUpdateTask() throws Exception {
      Task saved = new Task();
      saved.setTitle("Original");
      saved.setCompleted(false);
      saved.setUsername("testuser");
      saved = repository.save(saved);

      String body = """
        {"title": "Updated", "completed": true}
        """;

      mockMvc.perform(put("/api/tasks/" + saved.getId())
          .header("Authorization", "Bearer " + validToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated"))
        .andExpect(jsonPath("$.completed").value(true))
        .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("should return 404 when task does not exist")
    void shouldReturn404WhenNotFound() throws Exception {
      String body = """
        {"title": "Ghost task"}
        """;

      mockMvc.perform(put("/api/tasks/9999")
          .header("Authorization", "Bearer " + validToken)
          .contentType(MediaType.APPLICATION_JSON)
          .content(body))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 401 when no token on PUT")
    void shouldReturn401WithoutToken() throws Exception {
      mockMvc.perform(put("/api/tasks/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
            {"title": "No auth"}
            """))
        .andExpect(status().isUnauthorized());
    }
  }

  // ========== DELETE /api/tasks/{id} ==========
  @DisplayName("DELETE /api/tasks/{id}")
  class DeleteEndpoint {

    @Test
    @DisplayName("should delete an existing task")
    void shouldDeleteTask() throws Exception {
      Task saved = new Task();
      saved.setTitle("To delete");
      saved.setUsername("testuser");
      saved = repository.save(saved);

      mockMvc.perform(delete("/api/tasks/" + saved.getId())
          .header("Authorization", "Bearer " + validToken))
        .andExpect(status().isNoContent());

      // Verify it's gone
      mockMvc.perform(get("/api/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent task")
    void shouldReturn404WhenNotFound() throws Exception {
      mockMvc.perform(delete("/api/tasks/9999")
          .header("Authorization", "Bearer " + validToken))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return 401 when no token on DELETE")
    void shouldReturn401WithoutToken() throws Exception {
      mockMvc.perform(delete("/api/tasks/1"))
        .andExpect(status().isUnauthorized());
    }
  }
}
