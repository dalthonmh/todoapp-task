package com.example.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  @Autowired
  private TaskService service;
  
  // GET all tasks
  @GetMapping
  public ResponseEntity<List<Task>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  // POST create a task
  @PostMapping
  public ResponseEntity<Task> create(
    @RequestBody Task task,
    HttpServletRequest request) {

    String username = (String) request.getAttribute("username");
    task.setUsername(username);
    return ResponseEntity.status(HttpStatus.CREATED)
    .body(service.create(task));
  }

  // PUT update a task
  @PutMapping("/{id}")
  public ResponseEntity<Task> update(
    @PathVariable Long id,
    @RequestBody Task task,
    HttpServletRequest request) {

    String username = (String) request.getAttribute("username");
    task.setUsername(username);
    return ResponseEntity.ok(service.update(id, task));
  }

  // DELETE a task
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
