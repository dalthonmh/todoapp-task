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

  public Task update(Long id, Task taskBody) {
    Task task = repository.findById(id)
      .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    
    // Update only non-null fields
    if (taskBody.getTitle() != null) {
      task.setTitle(taskBody.getTitle());
    }
    if (taskBody.getUsername() != null) {
      task.setUsername(taskBody.getUsername());
    }
    task.setCompleted(taskBody.isCompleted());
    
    return repository.save(task);
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new TaskNotFoundException("Task not found with id: " + id);
    }
    repository.deleteById(id);
  }
}
