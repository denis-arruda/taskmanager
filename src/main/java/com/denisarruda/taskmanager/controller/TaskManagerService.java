package com.denisarruda.taskmanager.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;
import com.denisarruda.taskmanager.persistence.TaskJpa;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TaskManagerService {

  private final TaskJpa taskJpa;

  public TaskManagerService(TaskJpa taskJpa) {
    this.taskJpa = taskJpa;
  }

  public Task createTask(Task task) {
    Objects.requireNonNull(task, "Task cannot be null");
    return taskJpa.saveTask(task);
  }

  public Task getTask(String id) {
    return taskJpa.getTask(id);
  }

  public Task updateTask(Task updatedTask) {
    Objects.requireNonNull(updatedTask, "Updated task cannot be null");
    return taskJpa.updateTask(updatedTask);
  }

  public void deleteTask(String id) {
    taskJpa.deleteTask(id);
  }

  public List<Task> getTasks() {
    return taskJpa.getAllTasks();
  }

  public List<Task> getTasksByStatusAndDueDate(Status status, LocalDate dueDate, String sortBy, String sortOrder) {
    List<Task> allTasks = taskJpa.getAllTasks();

    // Filtering
    if (status != null) {
      allTasks = allTasks.stream()
          .filter(task -> task.status() == status)
          .collect(Collectors.toList());
    }
    if (dueDate != null) {
      allTasks = allTasks.stream()
          .filter(task -> task.dueDate() != null && task.dueDate().equals(dueDate)) // Check for null dueDate
          .collect(Collectors.toList());
    }

    // Sorting
    Comparator<Task> comparator = getComparator(sortBy);
    if ("desc".equalsIgnoreCase(sortOrder)) {
      comparator = comparator.reversed();
    }
    allTasks.sort(comparator);

    return allTasks;
  }

  private Comparator<Task> getComparator(String sortBy) {
    return switch (sortBy) {
      case "status" -> Comparator.comparing(Task::status);
      case "dueDate" -> Comparator.comparing(Task::dueDate);
      default -> throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
    };
  }
}