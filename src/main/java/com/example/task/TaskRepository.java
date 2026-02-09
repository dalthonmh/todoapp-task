package com.example.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findAll();
  // Spring generate it automatically

  Task save(Task task);
}