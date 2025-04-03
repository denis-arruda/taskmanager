package com.denisarruda.taskmanager.persistence;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;

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
    Document taskDocument = getCollection().find(eq("_id", new ObjectId(id))).first();
    return Optional.ofNullable(taskDocument);
  }

  public String save(Document taskDocument) {
    InsertOneResult result = getCollection().insertOne(taskDocument);
    return result.getInsertedId().asObjectId().getValue().toHexString();
  }

  public void update(Document taskDocument) {
    Document query = new Document();
    query.append("_id", taskDocument.getObjectId("_id"));

    Document update = new Document();
    update.append("$set", taskDocument);
    getCollection().updateOne(query, update);
  }

  private MongoCollection<Document> getCollection() {
    return mongoClient.getDatabase("task").getCollection("task");
  }

  public void deleteById(String id) {
    getCollection().deleteOne(new Document("_id", new ObjectId(id)));
  }
}