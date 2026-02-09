package com.example.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
  @Autowired
  private TaskService service;
  

  @GetMapping
  public ResponseEntity<List<Task>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @PostMapping
  public ResponseEntity<Task> create(@RequestBody Task task) {
    return ResponseEntity.status(HttpStatus.CREATED)
    .body(service.create(task));
  }
}