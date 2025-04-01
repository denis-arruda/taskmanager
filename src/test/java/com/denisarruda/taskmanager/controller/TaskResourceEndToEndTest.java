package com.denisarruda.taskmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.denisarruda.taskmanager.TestcontainersConfiguration;
import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskResourceEndToEndTest {

  @LocalServerPort
  private int port = 8080;

  private TestRestTemplate restTemplate;

  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/api/tasks";
    restTemplate = new TestRestTemplate();
  }

  @Test
  void testCreateTask() throws Exception {
    // Given
    Task newTask = new Task(null, "Test Task", "Test Description", LocalDate.now(), Status.PENDING);

    // When
    ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, newTask, Task.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    Task createdTask = response.getBody();
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
    ResponseEntity<Task> createdTaskResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
    assertThat(createdTaskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    String createdTaskId = createdTaskResponse.getBody().id();

    // 2. Now retrieve the task using the generated ID
    ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + createdTaskId, Task.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Task retrievedTask = response.getBody();
    assertThat(retrievedTask).isNotNull();
    assertThat(retrievedTask.id()).isEqualTo(createdTaskId);
  }

  @Test
  void testUpdateTask() throws Exception {
    // 1. Create a task to update
    Task newTask = new Task(null, "Task to Update", "Description", LocalDate.now(), Status.PENDING);
    ResponseEntity<Task> createdTaskResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
    assertThat(createdTaskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    Task createdTask = createdTaskResponse.getBody();

    // 2. Update the task
    Task updatedTask = new Task(createdTask.id(), "Updated Task", "Updated Description",
        LocalDate.now().plusDays(1), Status.IN_PROGRESS);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Task> requestEntity = new HttpEntity<>(updatedTask, headers);

    ResponseEntity<Task> response = restTemplate.exchange(baseUrl, org.springframework.http.HttpMethod.PUT,
        requestEntity, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Task updatedTaskFromResponse = response.getBody();
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
    ResponseEntity<Task> createdTaskResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
    assertThat(createdTaskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    String createdTaskId = createdTaskResponse.getBody().id();

    // 2. Delete the task
    restTemplate.delete(baseUrl + "/" + createdTaskId);

    // 3. Verify that the task is no longer available
    ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/" + createdTaskId, String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetTasksByStatusAndDueDate() throws Exception {
    // 1. Create some tasks with known statuses and due dates
    Task task1 = new Task(null, "Task 1", "Description 1", LocalDate.now(), Status.PENDING);
    Task task2 = new Task(null, "Task 2", "Description 2", LocalDate.now().plusDays(1), Status.IN_PROGRESS);
    Task task3 = new Task(null, "Task 3", "Description 3", LocalDate.now(), Status.COMPLETED);

    restTemplate.postForEntity(baseUrl, task1, Task.class);
    restTemplate.postForEntity(baseUrl, task2, Task.class);
    restTemplate.postForEntity(baseUrl, task3, Task.class);

    // 2. Test filtering by status
    ResponseEntity<Task[]> responseByStatus = restTemplate.getForEntity(baseUrl + "?status=PENDING",
        Task[].class);
    assertThat(responseByStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseByStatus.getBody()).hasSize(1);
    assertThat(responseByStatus.getBody()[0].title()).isEqualTo("Task 1");

    // 3. Test filtering by dueDate
    ResponseEntity<Task[]> responseByDueDate = restTemplate.getForEntity(
        baseUrl + "?dueDate=" + LocalDate.now(),
        Task[].class);
    assertThat(responseByDueDate.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseByDueDate.getBody()).hasSize(2);

    // 4. Test sorting - This is a bit more complex with TestRestTemplate,
    // you might need to inspect the response headers or rely on the order being
    // consistent for now.
    ResponseEntity<Task[]> responseSortedByDate = restTemplate
        .getForEntity(baseUrl + "?sortBy=dueDate&sortOrder=desc", Task[].class);
    assertThat(responseSortedByDate.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseSortedByDate.getBody()).hasSize(3);
    assertThat(responseSortedByDate.getBody()[0].title()).isEqualTo("Task 2");
    assertThat(responseSortedByDate.getBody()[1].title()).isEqualTo("Task 1");
    assertThat(responseSortedByDate.getBody()[2].title()).isEqualTo("Task 3");
  }
}