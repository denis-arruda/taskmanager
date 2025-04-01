package com.denisarruda.taskmanager.persistence;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.denisarruda.taskmanager.domain.Status;

@Document("tasks") // Specify the collection name
public record TaskDocument(
    @Id String id, // MongoDB will generate this automatically
    String title,
    String description,
    LocalDate dueDate,
    Status status) {
}