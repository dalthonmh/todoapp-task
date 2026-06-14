package com.example.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  // Return tasks sorted with incomplete (false) first, then completed (true) last.
  // Among tasks with the same completed status, show newest first (by creation date).
  // This keeps the listing behavior consistent with todoapp-core.
  List<Task> findAllByOrderByCompletedAscCreatedAtDesc();
}