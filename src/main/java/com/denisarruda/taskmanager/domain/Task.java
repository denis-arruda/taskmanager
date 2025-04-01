package com.denisarruda.taskmanager.domain;

import java.time.LocalDate;

public record Task(String id, String title, String description, LocalDate dueDate, Status status) {
}