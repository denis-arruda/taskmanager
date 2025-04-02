package com.denisarruda.taskmanager.persistence;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;

import org.springframework.stereotype.Service;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

@Service
public class TaskJpa {

  private final TaskRepository taskRepository;

  public TaskJpa(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Task saveTask(Task task) {
    Document taskDocument = toTaskDocument(task);
    String newTaskId = taskRepository.save(taskDocument);
    return toTask(taskRepository.findById(newTaskId));
  }

  public Task getTask(String id) {
    Optional<Document> taskDocument = taskRepository.findById(id);
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
    List<Document> taskDocuments = taskRepository.findAll();
    return taskDocuments.stream()
        .map(this::toTask)
        .collect(Collectors.toList());
  }

  private Document toTaskDocument(Task task) {
    Document taskDocument = new Document();
    taskDocument.append("id", task.id());
    taskDocument.append("title", task.title());
    taskDocument.append("description", task.description());
    taskDocument.append("dueDate", Date.from(task.dueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    taskDocument.append("status", task.status());
    return taskDocument;
  }

  private Task toTask(Optional<Document> taskDocument) {
    return taskDocument.map(this::toTask).orElse(null);
  }

  private Task toTask(Document taskDocument) {
    return new Task(
        taskDocument.getString("id"),
        taskDocument.getString("title"),
        taskDocument.getString("description"),
        taskDocument.getDate("dueDate").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
        taskDocument.get("status", Status.class));
  }
}