package com.denisarruda.taskmanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;
import com.denisarruda.taskmanager.persistence.TaskJpa;

class TaskManagerServiceTest {

  @Mock
  private TaskJpa taskJpa;

  @InjectMocks
  private TaskManagerService taskManagerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTask() {
    Task task = new Task("1", "Test Task", "Test Description", LocalDate.now(), Status.PENDING); // Corrected order
    when(taskJpa.saveTask(task)).thenReturn(task);

    Task createdTask = taskManagerService.createTask(task);

    assertEquals(task, createdTask);
    verify(taskJpa, times(1)).saveTask(task);
  }

  @Test
  void testGetTask() {
    String taskId = "1";
    Task task = new Task(taskId, "Test Task", "Test Description", LocalDate.now(), Status.PENDING); // Corrected order
    when(taskJpa.getTask(taskId)).thenReturn(task);

    Task retrievedTask = taskManagerService.getTask(taskId);

    assertEquals(task, retrievedTask);
    verify(taskJpa, times(1)).getTask(taskId);
  }

  @Test
  void testUpdateTask() {
    Task updatedTask = new Task("1", "Updated Task", "Updated Description", LocalDate.now().plusDays(1),
        Status.IN_PROGRESS); // Corrected order
    when(taskJpa.updateTask(updatedTask)).thenReturn(updatedTask);

    Task result = taskManagerService.updateTask(updatedTask);

    assertEquals(updatedTask, result);
    verify(taskJpa, times(1)).updateTask(updatedTask);
  }

  @Test
  void testDeleteTask() {
    String taskId = "1";
    taskManagerService.deleteTask(taskId);
    verify(taskJpa, times(1)).deleteTask(taskId);
  }

  @Test
  void testGetTasks() {
    List<Task> tasks = new ArrayList<>();
    tasks.add(new Task("1", "Task 1", "Description 1", LocalDate.now(), Status.PENDING));
    tasks.add(new Task("2", "Task 2", "Description 2", LocalDate.now().plusDays(1), Status.IN_PROGRESS));
    when(taskJpa.getAllTasks()).thenReturn(tasks);

    List<Task> retrievedTasks = taskManagerService.getTasks();

    assertEquals(tasks, retrievedTasks);
    verify(taskJpa, times(1)).getAllTasks();
  }

  @Test
  void testGetTasksByStatusAndDueDate() {
    Status status = Status.IN_PROGRESS;
    LocalDate dueDate = LocalDate.now();
    String sortBy = "dueDate";
    String sortOrder = "asc";

    List<Task> tasks = new ArrayList<>();
    tasks.add(new Task("1", "Task 1", "Description 1", LocalDate.now(), Status.IN_PROGRESS));
    tasks.add(new Task("2", "Task 2", "Description 2", LocalDate.now().plusDays(1), Status.PENDING));
    when(taskJpa.getAllTasks()).thenReturn(tasks);

    List<Task> filteredTasks = taskManagerService.getTasksByStatusAndDueDate(status, dueDate, sortBy, sortOrder);

    assertEquals(1, filteredTasks.size());
    assertEquals("1", filteredTasks.get(0).id());
  }
}