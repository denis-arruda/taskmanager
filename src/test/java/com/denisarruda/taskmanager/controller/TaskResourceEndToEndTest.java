package com.denisarruda.taskmanager.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.denisarruda.taskmanager.domain.Status;
import com.denisarruda.taskmanager.domain.Task;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
class TaskResourceEndToEndTest {

  @InjectMock
  TaskManagerService taskManagerService;

  @Test
  void testCreateTask() throws Exception {

    Task newTask = new Task("3632", "Test Task", "Test Description", LocalDate.now(), Status.PENDING);

    Mockito.when(taskManagerService.createTask(Mockito.any(Task.class)))
        .thenReturn(newTask);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newTask)
        .when()
        .post("/api/tasks")
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(
            "id", notNullValue(),
            "title", is("Test Task"),
            "description", is("Test Description"),
            "dueDate", is(LocalDate.now().toString()),
            "status", is(Status.PENDING.toString()));

    Mockito.verify(taskManagerService, times(1)).createTask(Mockito.any(Task.class));
    Mockito.verifyNoMoreInteractions(this.taskManagerService);
  }

  @Test
  void testGetTaskById() throws Exception {
    // 1. Create a task first to get a valid ID
    Task newTask = new Task("6033", "Task for GET", "Description", LocalDate.now(), Status.PENDING);

    Mockito.when(taskManagerService.getTask(Mockito.eq(newTask.id())))
        .thenReturn(newTask);

    given()
        .when()
        .get("/api/tasks/" + newTask.id())
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(
            "id", is(newTask.id()),
            "title", is(newTask.title()),
            "description", is(newTask.description()),
            "dueDate", is(LocalDate.now().toString()),
            "status", is(Status.PENDING.toString()));

    Mockito.verify(taskManagerService, times(1)).getTask(Mockito.eq(newTask.id()));
    Mockito.verifyNoMoreInteractions(this.taskManagerService);
  }

  @Test
  void testUpdateTask() throws Exception {
    // 1. Create a task to update
    Task newTask = new Task("8234", "Task to Update", "Description", LocalDate.now(), Status.IN_PROGRESS);

    Mockito.when(taskManagerService.updateTask(Mockito.any(Task.class)))
        .thenReturn(newTask);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(newTask)
        .when()
        .put("/api/tasks")
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(
            "id", is(newTask.id()),
            "title", is(newTask.title()),
            "description", is(newTask.description()),
            "dueDate", is(LocalDate.now().toString()),
            "status", is(Status.IN_PROGRESS.toString()));

    Mockito.verify(taskManagerService, times(1)).updateTask(Mockito.any(Task.class));
    Mockito.verifyNoMoreInteractions(this.taskManagerService);
  }

  @Test
  void testDeleteTask() throws Exception {

    given()
        .when()
        .delete("/api/tasks/11726")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());

    Mockito.verify(taskManagerService, times(1)).deleteTask(Mockito.eq("11726"));
    Mockito.verifyNoMoreInteractions(this.taskManagerService);
  }

  @Test
  void testGetTasksByStatusAndDueDate() throws Exception {
    // 1. Create some tasks with known statuses and due dates
    Task task1 = new Task("12930", "Task 1", "Description 1", LocalDate.now().plusDays(1), Status.PENDING);
    Task task2 = new Task("13032", "Task 2", "Description 2", LocalDate.now().plusDays(2), Status.PENDING);
    Task task3 = new Task("13131", "Task 3", "Description 3", LocalDate.now().plusDays(3), Status.PENDING);

    Mockito
        .when(taskManagerService.getTasksByStatusAndDueDate(Mockito.eq(Status.PENDING), Mockito.isNull(),
            Mockito.eq("dueDate"), Mockito.eq("asc")))
        .thenReturn(Arrays.asList(task1, task2, task3));

    given()
        .when()
        .get("/api/tasks?status=PENDING&sortBy=dueDate&sortOrder=asc")
        .then()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.JSON)
        .body(
            "[0].id", is(task1.id()),
            "[0].title", is(task1.title()),
            "[0].description", is(task1.description()),
            "[0].dueDate", is(task1.dueDate().toString()),
            "[0].status", is(Status.PENDING.toString()),

            "[1].id", is(task2.id()),
            "[1].title", is(task2.title()),
            "[1].description", is(task2.description()),
            "[1].dueDate", is(task2.dueDate().toString()),
            "[1].status", is(Status.PENDING.toString()),

            "[2].id", is(task3.id()),
            "[2].title", is(task3.title()),
            "[2].description", is(task3.description()),
            "[2].dueDate", is(task3.dueDate().toString()),
            "[2].status", is(Status.PENDING.toString()));

    Mockito.verify(taskManagerService, times(1)).getTasksByStatusAndDueDate(Mockito.eq(Status.PENDING),
        Mockito.isNull(),
        Mockito.eq("dueDate"), Mockito.eq("asc"));
    Mockito.verifyNoMoreInteractions(this.taskManagerService);
  }
}