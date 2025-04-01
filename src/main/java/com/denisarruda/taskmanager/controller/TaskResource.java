package com.denisarruda.taskmanager.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Resource", description = "Endpoints for managing tasks")
public class TaskResource {

  private final TaskManagerService taskService; // Assuming you'll create this class

  public TaskResource(TaskManagerService taskResourceManager) {
    this.taskService = taskResourceManager;
  }

  @PostMapping
  @Operation(summary = "Create a new task", description = "Creates a new task with the provided information.")
  public Task createTask(@RequestBody TaskInput taskInput) {
    // Convert TaskInput to Task
    Task task = new Task(null, taskInput.title(), taskInput.description(), taskInput.dueDate(), taskInput.status());
    return taskService.createTask(task);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get task by ID", description = "Retrieves a task by its unique identifier.")
  public ResponseEntity<?> getTaskById(@PathVariable String id) {
    var output = taskService.getTask(id);
    if (output == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(output);
  }

  @PutMapping
  @Operation(summary = "Update a task", description = "Updates an existing task with the provided information.")
  public Task updateTask(@RequestBody Task updatedTask) {
    return taskService.updateTask(updatedTask);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a task", description = "Deletes a task by its unique identifier.")
  public void deleteTask(@PathVariable String id) {
    taskService.deleteTask(id);
  }

  @GetMapping
  @Operation(summary = "Get tasks by status and due date", description = "Retrieves a list of tasks filtered by status and/or due date.")
  public List<Task> getTasksByStatusAndDueDate(
      @RequestParam(value = "status", required = false) Status status,
      @RequestParam(value = "dueDate", required = false) LocalDate dueDate, // Add dueDate parameter
      @RequestParam(value = "sortBy", required = false, defaultValue = "dueDate") String sortBy,
      @RequestParam(value = "sortOrder", required = false, defaultValue = "asc") String sortOrder) {
    return taskService.getTasksByStatusAndDueDate(status, dueDate, sortBy, sortOrder);
  }
}

record TaskInput(String title, String description, LocalDate dueDate, Status status) {
}
