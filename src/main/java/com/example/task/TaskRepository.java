package com.example.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  // JpaRepository already provides: findAll(), findById(), save(),
  // deleteById(), existsById(), count(), etc.
  // Only custom query methods here.
}