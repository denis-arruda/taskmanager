# Task Manager API

This project provides a simple REST API and a web interface for managing tasks. It's built with Spring Boot and provides endpoints for creating, retrieving, updating, and deleting tasks.

## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21:** You can download the appropriate JDK for your operating system from [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/).
- **Docker:** Download and install Docker Desktop from [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/).
- **Docker Compose:** Docker Compose is usually included with Docker Desktop. If not, follow the instructions for your OS on the Docker website.

## Running the Application

1. **Build and Run:**

   - Open a terminal and navigate to the root directory of the project.
   - Run the following command:
     ```bash
     ./mvnw spring-boot:run
     ```
   - This command will start the Spring Boot application. You should see log messages indicating that the application is starting up.

2. **Access the API:**
   - Once the application is running, you can access the API endpoints at `http://localhost:8080/api/tasks`.

## API Documentation

You can find the API documentation [here](http://localhost:8080/swagger-ui.html).

## Running Tests

To run the unit and integration tests for this application, use the following command:

```bash
./mvnw test
```
