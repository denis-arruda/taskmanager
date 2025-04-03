# Task Manager API

This project provides a simple REST API and a web interface for managing tasks. It is built using Quarkus and provides endpoints for creating, retrieving, updating, and deleting tasks. Additionally, there is a corresponding implementation of this project using Spring Boot available in the `spring` branch of this repository.

## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21:** You can download the appropriate JDK for your operating system from [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/).
- **MongoDB:** You can find installation instructions for MongoDB on the official MongoDB website at [https://www.mongodb.com/docs/manual/installation/](https://www.mongodb.com/docs/manual/installation/).

## Running the Application

1. **Build and Run:**

   - Open a terminal and navigate to the root directory of the project.
   - Run the following command to start the Quarkus application in development mode:
     ```bash
     ./mvnw quarkus:dev
     ```
   - This command will start the Quarkus application. You should see log messages indicating that the application is starting up.

2. **Access the API:**

   - Once the application is running, you can access the API endpoints at http://localhost:8080/api/tasks/.

3. **OpenAPI Documentation:**
   - The OpenAPI documentation for the API is available at http://localhost:8080/q/swagger-ui/.

## Running Tests

To run the unit and integration tests for this application, use the following command:

```bash
./mvnw test
```
