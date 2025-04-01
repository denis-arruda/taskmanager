package com.denisarruda.taskmanager.persistence;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

class TaskJpaTest {

  @Mock
  private TaskRepository taskRepository;

  @InjectMocks
  private TaskJpa taskJpa;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSaveTask() {
    Task task = new Task("1", "Test Task", "Test Description", LocalDate.now(), Status.PENDING);
    TaskDocument taskDocument = new TaskDocument("1", "Test Task", "Test Description", LocalDate.now(), Status.PENDING);
    when(taskRepository.save(any(TaskDocument.class))).thenReturn(taskDocument);

    Task savedTask = taskJpa.saveTask(task);

    assertEquals(task, savedTask);
    verify(taskRepository, times(1)).save(any(TaskDocument.class));
  }

  @Test
  void testGetTask() {
    String taskId = "1";
    TaskDocument taskDocument = new TaskDocument(taskId, "Test Task", "Test Description", LocalDate.now(),
        Status.PENDING);
    when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskDocument));

    Task retrievedTask = taskJpa.getTask(taskId);

    assertEquals(taskDocument.id(), retrievedTask.id());
    assertEquals(taskDocument.title(), retrievedTask.title());
    assertEquals(taskDocument.description(), retrievedTask.description());
    verify(taskRepository, times(1)).findById(taskId);
  }

  @Test
  void testGetTaskNotFound() {
    String taskId = "1";
    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertNull(taskJpa.getTask(taskId));
    verify(taskRepository, times(1)).findById(taskId);
  }

  @Test
  void testUpdateTask() {
    Task updatedTask = new Task("1", "Updated Task", "Updated Description", LocalDate.now(), Status.IN_PROGRESS);
    TaskDocument updatedTaskDocument = new TaskDocument("1", "Updated Task", "Updated Description", LocalDate.now(),
        Status.IN_PROGRESS);
    when(taskRepository.save(any(TaskDocument.class))).thenReturn(updatedTaskDocument);

    Task result = taskJpa.updateTask(updatedTask);

    assertEquals(updatedTask, result);
    verify(taskRepository, times(1)).save(any(TaskDocument.class));
  }

  @Test
  void testDeleteTask() {
    String taskId = "1";
    taskJpa.deleteTask(taskId);
    verify(taskRepository, times(1)).deleteById(taskId);
  }

  @Test
  void testGetAllTasks() {
    List<TaskDocument> taskDocuments = new ArrayList<>();
    taskDocuments.add(new TaskDocument("1", "Task 1", "Description 1", LocalDate.now(), Status.PENDING));
    taskDocuments
        .add(new TaskDocument("2", "Task 2", "Description 2", LocalDate.now().plusDays(1), Status.IN_PROGRESS));
    when(taskRepository.findAll()).thenReturn(taskDocuments);

    List<com.denisarruda.taskmanager.domain.Task> retrievedTasks = taskJpa.getAllTasks();

    assertEquals(taskDocuments.size(), retrievedTasks.size());
    verify(taskRepository, times(1)).findAll();
  }
}