package com.denisarruda.taskmanager.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
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
    Document taskDocument = new Document("id", "1").append("title", "Test Task")
        .append("description", "Test Description").append("dueDate", new Date())
        .append("status", Status.PENDING);

    when(taskRepository.save(any(Document.class))).thenReturn(task.id());
    when(taskRepository.findById(task.id())).thenReturn(Optional.of(taskDocument));
    Task savedTask = taskJpa.saveTask(task);

    assertEquals(task, savedTask);
    verify(taskRepository, times(1)).save(any(Document.class));
    verify(taskRepository, times(1)).findById(task.id());
  }

  @Test
  void testGetTask() {
    String taskId = "1";
    Document taskDocument = new Document("id", taskId).append("title", "Test Task")
        .append("description", "Test Description").append("dueDate", new Date())
        .append("status", Status.PENDING);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskDocument));

    Task retrievedTask = taskJpa.getTask(taskId);

    assertEquals(taskDocument.getString("id"), retrievedTask.id());
    assertEquals(taskDocument.getString("title"), retrievedTask.title());
    assertEquals(taskDocument.getString("description"), retrievedTask.description());
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
    Document taskDocument = new Document("id", "1").append("title", "Updated Task")
        .append("description", "Updated Description").append("dueDate", new Date())
        .append("status", Status.IN_PROGRESS);
    when(taskRepository.findById(updatedTask.id())).thenReturn(Optional.of(taskDocument));
    when(taskRepository.save(any(Document.class))).thenReturn(updatedTask.id());

    Task result = taskJpa.updateTask(updatedTask);

    assertEquals(updatedTask, result);
    verify(taskRepository, times(1)).save(any(Document.class));
  }

  @Test
  void testDeleteTask() {
    String taskId = "1";
    taskJpa.deleteTask(taskId);
    verify(taskRepository, times(1)).deleteById(taskId);
  }

  @Test
  void testGetAllTasks() {
    List<Document> taskDocuments = new ArrayList<>();
    Document taskDocument1 = new Document("id", "1").append("title", "Task 1")
        .append("description", "Description 1").append("dueDate", new Date()).append("status", Status.PENDING);
    Document taskDocument2 = new Document("id", "2").append("title", "Task 2")
        .append("description", "Description 2").append("dueDate", new Date())
        .append("status", Status.IN_PROGRESS);
    taskDocuments.add(taskDocument1);
    taskDocuments.add(taskDocument2);

    when(taskRepository.findAll()).thenReturn(taskDocuments);

    List<Task> retrievedTasks = taskJpa.getAllTasks();

    assertEquals(taskDocuments.size(), retrievedTasks.size());
    verify(taskRepository, times(1)).findAll();
  }
}