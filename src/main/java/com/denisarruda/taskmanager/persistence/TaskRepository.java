package com.denisarruda.taskmanager.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<TaskDocument, String> {

  // You can add custom query methods here if needed, for example:
  // List<TaskDocument> findByStatus(Status status);
}
