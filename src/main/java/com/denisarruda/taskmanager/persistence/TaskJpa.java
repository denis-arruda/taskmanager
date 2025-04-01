package com.denisarruda.taskmanager.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.denisarruda.taskmanager.domain.Task;

@Service
public class TaskJpa {

  private final TaskRepository taskRepository;

  public TaskJpa(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Task saveTask(Task task) {
    TaskDocument taskDocument = toTaskDocument(task);
    TaskDocument savedTaskDocument = taskRepository.save(taskDocument);
    return toTask(savedTaskDocument);
  }

  public Task getTask(String id) {
    Optional<TaskDocument> taskDocument = taskRepository.findById(id);
    return toTask(taskDocument);
  }

  public Task updateTask(Task task) {
    // You might want to add logic to check if the task exists before updating
    return saveTask(task);
  }

  public void deleteTask(String id) {
    taskRepository.deleteById(id);
  }

  public List<Task> getAllTasks() {
    List<TaskDocument> taskDocuments = taskRepository.findAll();
    return taskDocuments.stream()
        .map(this::toTask)
        .collect(Collectors.toList());
  }

  private TaskDocument toTaskDocument(Task task) {
    return new TaskDocument(task.id(), task.title(), task.description(), task.dueDate(), task.status());
  }

  private Task toTask(Optional<TaskDocument> taskDocument) {
    return taskDocument.map(this::toTask).orElse(null);
  }

  private Task toTask(TaskDocument taskDocument) {
    return new Task(taskDocument.id(), taskDocument.title(), taskDocument.description(), taskDocument.dueDate(),
        taskDocument.status());
  }
}