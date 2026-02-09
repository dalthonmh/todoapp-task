package com.example.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Task")
public class TaskNotFoundException extends RuntimeException {
  public TaskNotFoundException(String id) {
    super("Task not found with ID: " + id);
  }
}
