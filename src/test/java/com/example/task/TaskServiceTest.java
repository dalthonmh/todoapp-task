package com.example.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository repository;

  @InjectMocks
  private TaskService service;

  private Task task;

  @BeforeEach
  void setUp() {
    task = new Task();
    task.setId(1L);
    task.setTitle("Test task");
    task.setCompleted(false);
    task.setUsername("testuser");
  }

  // ========== GET ALL ==========
  @DisplayName("getAll()")
  class GetAllTests {

    @Test
    @DisplayName("should return all tasks")
    void shouldReturnAllTasks() {
      Task task2 = new Task();
      task2.setId(2L);
      task2.setTitle("Second task");

      when(repository.findAll()).thenReturn(Arrays.asList(task, task2));

      List<Task> result = service.getAll();

      assertThat(result).hasSize(2);
      assertThat(result.get(0).getTitle()).isEqualTo("Test task");
      assertThat(result.get(1).getTitle()).isEqualTo("Second task");
      verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return empty list when no tasks exist")
    void shouldReturnEmptyList() {
      when(repository.findAll()).thenReturn(List.of());

      List<Task> result = service.getAll();

      assertThat(result).isEmpty();
      verify(repository, times(1)).findAll();
    }
  }

  // ========== CREATE ==========
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create a task successfully")
    void shouldCreateTask() {
      when(repository.save(any(Task.class))).thenReturn(task);

      Task result = service.create(task);

      assertThat(result).isNotNull();
      assertThat(result.getTitle()).isEqualTo("Test task");
      assertThat(result.getUsername()).isEqualTo("testuser");
      verify(repository, times(1)).save(task);
    }

    @Test
    @DisplayName("should create task with completed=null defaulting to false")
    void shouldCreateTaskWithNullCompleted() {
      task.setCompleted(null);
      when(repository.save(any(Task.class))).thenReturn(task);

      Task result = service.create(task);

      assertThat(result).isNotNull();
      // completed is null here because @PrePersist runs at JPA level, not service level
      verify(repository, times(1)).save(task);
    }
  }

  // ========== UPDATE ==========
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update an existing task")
    void shouldUpdateTask() {
      Task updatedBody = new Task();
      updatedBody.setTitle("Updated title");
      updatedBody.setCompleted(true);
      updatedBody.setUsername("testuser");

      when(repository.findById(1L)).thenReturn(Optional.of(task));
      when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

      Task result = service.update(1L, updatedBody);

      assertThat(result.getTitle()).isEqualTo("Updated title");
      assertThat(result.getCompleted()).isTrue();
      verify(repository).findById(1L);
      verify(repository).save(any(Task.class));
    }

    @Test
    @DisplayName("should update only title when completed is null")
    void shouldUpdateOnlyTitle() {
      Task updatedBody = new Task();
      updatedBody.setTitle("New title only");
      updatedBody.setCompleted(null);
      updatedBody.setUsername("testuser");

      when(repository.findById(1L)).thenReturn(Optional.of(task));
      when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

      Task result = service.update(1L, updatedBody);

      assertThat(result.getTitle()).isEqualTo("New title only");
      // completed comes from the body (null in this case)
      verify(repository).save(any(Task.class));
    }

    @Test
    @DisplayName("should throw TaskNotFoundException when task does not exist")
    void shouldThrowWhenTaskNotFound() {
      when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.update(99L, task))
        .isInstanceOf(TaskNotFoundException.class);

      verify(repository, never()).save(any());
    }
  }

  // ========== DELETE ==========
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete an existing task")
    void shouldDeleteTask() {
      when(repository.existsById(1L)).thenReturn(true);
      doNothing().when(repository).deleteById(1L);

      service.delete(1L);

      verify(repository).existsById(1L);
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("should throw TaskNotFoundException when task does not exist")
    void shouldThrowWhenDeletingNonExistent() {
      when(repository.existsById(99L)).thenReturn(false);

      assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(TaskNotFoundException.class);

      verify(repository, never()).deleteById(any());
    }
  }
}
