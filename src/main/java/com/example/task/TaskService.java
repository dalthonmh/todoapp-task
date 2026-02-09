package com.example.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
  @Autowired
  private TaskRepository repository;

  public List<Task> getAll() {
    return repository.findAll();
  }
    
  public Task create(Task task) {
    return repository.save(task);
  }
}
