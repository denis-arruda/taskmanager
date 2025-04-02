package com.denisarruda.taskmanager.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TaskRepository {

  @Inject
  MongoClient mongoClient;

  public List<Document> findAll() {
    return getCollection().find().into(new ArrayList<>());
  }

  public Optional<Document> findById(String id) {
    Document taskDocument = getCollection().find(new Document("id", id)).first();
    return Optional.ofNullable(taskDocument);
  }

  public String save(Document taskDocument) {
    InsertOneResult result = getCollection().insertOne(taskDocument);
    return result.getInsertedId().asString().getValue();
  }

  public void update(Document taskDocument) {
    String id = taskDocument.getString("id");
    getCollection().replaceOne(new Document("id", id), taskDocument);
  }

  private MongoCollection<Document> getCollection() {
    return mongoClient.getDatabase("task").getCollection("task");
  }

  public void deleteById(String id) {
    getCollection().deleteOne(new Document("id", id));
  }
}