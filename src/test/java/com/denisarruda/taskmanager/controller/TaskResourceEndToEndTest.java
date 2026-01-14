package com.denisarruda.taskmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.denisarruda.taskmanager.TestcontainersConfiguration;
import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskResourceEndToEndTest {

  @LocalServerPort
  private int port;

  private RestTestClient client;

  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/api/tasks";
    client = RestTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build();
  }

  @Test
  void testCreateTask() throws Exception {
    // Given
    Task newTask = new Task(null, "Test Task", "Test Description", LocalDate.now(), Status.PENDING);

    // When
    Task createdTask = client.post().body(newTask).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();

    assertThat(createdTask).isNotNull();
    assertThat(createdTask.id()).isNotNull();
    assertThat(createdTask.title()).isEqualTo("Test Task");
    assertThat(createdTask.description()).isEqualTo("Test Description");
    assertThat(createdTask.dueDate()).isEqualTo(LocalDate.now());
    assertThat(createdTask.status()).isEqualTo(Status.PENDING);
  }

  @Test
  void testGetTaskById() throws Exception {
    // 1. Create a task first to get a valid ID
    Task newTask = new Task(null, "Task for GET", "Description", LocalDate.now(), Status.PENDING);

    Task createdTask = client.post().body(newTask).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();
    String createdTaskId = createdTask.id();

    // 2. Now retrieve the task using the generated ID
    Task retrievedTask = client.get().uri("/" + createdTaskId).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();

    // Then
    assertThat(retrievedTask).isNotNull();
    assertThat(retrievedTask.id()).isEqualTo(createdTaskId);
  }

  @Test
  void testUpdateTask() throws Exception {
    // 1. Create a task to update
    Task newTask = new Task(null, "Task to Update", "Description", LocalDate.now(), Status.PENDING);
    Task createdTask = client.post().body(newTask).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();

    // 2. Update the task
    Task updatedTask = new Task(createdTask.id(), "Updated Task", "Updated Description",
      LocalDate.now().plusDays(1), Status.IN_PROGRESS);

    Task updatedTaskFromResponse = client.put().body(updatedTask).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();

    assertThat(updatedTaskFromResponse).isNotNull();
    assertThat(updatedTaskFromResponse.id()).isEqualTo(createdTask.id());
    assertThat(updatedTaskFromResponse.title()).isEqualTo("Updated Task");
    assertThat(updatedTaskFromResponse.description()).isEqualTo("Updated Description");
    assertThat(updatedTaskFromResponse.dueDate()).isEqualTo(LocalDate.now().plusDays(1));
    assertThat(updatedTaskFromResponse.status()).isEqualTo(Status.IN_PROGRESS);
  }

  @Test
  void testDeleteTask() throws Exception {
    // 1. Create a task to delete
    Task newTask = new Task(null, "Task to Delete", "Description", LocalDate.now(), Status.PENDING);
    Task createdTask = client.post().body(newTask).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task>() {}).returnResult().getResponseBody();
    String createdTaskId = createdTask.id();

    // 2. Delete the task
    client.delete().uri("/" + createdTaskId).exchange().expectStatus().isOk();

    // 3. Verify that the task is no longer available
    client.get().uri("/" + createdTaskId).exchange().expectStatus().isNotFound();
  }

  @Test
  void testGetTasksByStatusAndDueDate() throws Exception {
    // 1. Create some tasks with known statuses and due dates
    Task task1 = new Task(null, "Task 1", "Description 1", LocalDate.now(), Status.PENDING);
    Task task2 = new Task(null, "Task 2", "Description 2", LocalDate.now().plusDays(1), Status.IN_PROGRESS);
    Task task3 = new Task(null, "Task 3", "Description 3", LocalDate.now(), Status.COMPLETED);

    client.post().body(task1).exchange().expectStatus().isOk();
    client.post().body(task2).exchange().expectStatus().isOk();
    client.post().body(task3).exchange().expectStatus().isOk();

    // 2. Test filtering by status
    Task[] tasksByStatus = client.get().uri("?status=PENDING").exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task[]>() {}).returnResult().getResponseBody();
    assertThat(tasksByStatus).hasSize(1);
    assertThat(tasksByStatus[0].title()).isEqualTo("Task 1");

    // 3. Test filtering by dueDate
    Task[] tasksByDueDate = client.get().uri("?dueDate=" + LocalDate.now()).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task[]>() {}).returnResult().getResponseBody();
    assertThat(tasksByDueDate).hasSize(2);

    // 4. Test sorting
    Task[] tasksSortedByDate = client.get().uri("?sortBy=dueDate&sortOrder=desc").exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<Task[]>() {}).returnResult().getResponseBody();
    assertThat(tasksSortedByDate).hasSize(3);
    assertThat(tasksSortedByDate[0].title()).isEqualTo("Task 2");
    assertThat(tasksSortedByDate[1].title()).isEqualTo("Task 1");
    assertThat(tasksSortedByDate[2].title()).isEqualTo("Task 3");
  }
}