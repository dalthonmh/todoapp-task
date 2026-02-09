package com.example.task;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
  private final JwtUtil jwtUtil;
  
  @Override
  public boolean preHandle(
          HttpServletRequest request, 
          HttpServletResponse response, 
          Object handler
  ) throws Exception {

    String method = request.getMethod();
    
    // GET is public method
    if ("GET".equals(method)) {
      return true;
    }

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"message\": \"Token required\"}");
      return false; // Block the request if no token is provided
    }

    String token = authHeader.substring(7);
    String username = jwtUtil.extractUsername(token);

    if (username == null) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json");
      response.getWriter().write("{\"message\": \"Invalid or expired token\"}");
      return false;
    }
    
    request.setAttribute("username", username);
    return true;
  }
}