# TaskFlow Project QA Test Plan

## 1. Introduction

This document provides a detailed testing strategy for the TaskFlow project's backend application. It serves as a technical blueprint for developers and QA personnel to write and execute tests. The primary goal is to ensure the application's reliability, correctness, and adherence to requirements by defining clear testing methodologies for each layer of the architecture: Controller, Service, and Repository.

## 2. Testing Philosophy

We will adopt a "test-after" approach for initial development, with a commitment to writing comprehensive tests immediately following feature implementation. Our strategy is centered around the **Test Pyramid**, emphasizing a strong foundation of unit tests, a solid layer of integration tests, and a focused set of end-to-end tests.

-   **Coverage Goal:** 80% line coverage for business logic (services) and controllers.
-   **Tools:** JUnit 5, Mockito, Spring Boot Test, H2 Database (for testing), Testcontainers (for future production-parity testing).

---

## 3. Repository Layer Testing

### 3.1. Objective

The primary goal of repository testing is to verify the correctness of the JPA entity mappings and the functionality of any custom query methods. We are not testing the Spring Data JPA framework itself, but rather our specific configuration and use of it.

### 3.2. Methodology

-   **Tooling:** Use the `@DataJpaTest` annotation. This annotation disables full auto-configuration and applies only configuration relevant to JPA tests. It uses an embedded in-memory database (H2) by default.
-   **Scope:** Each test will focus on a single repository interface.
-   **Data Management:** Use the `TestEntityManager` to persist and flush entities to the test database. This provides a clean state for each test method.

### 3.3. What to Test

1.  **Entity Mapping:**
    -   **Test Case:** Verify that an entity can be successfully persisted and retrieved.
    -   **Purpose:** Confirms that all `@Entity`, `@Column`, `@Id`, and relationship annotations (`@ManyToOne`, etc.) are correctly configured.
2.  **Custom Query Methods:**
    -   **Test Case:** For each custom method in the repository (e.g., `findByEmail(String email)`), write a test to verify it returns the expected result.
    -   **Purpose:** Ensures the query is syntactically correct and the logic is sound.
    -   **Example:** For `findByEmail`, test one scenario where the user exists and one where they do not.
3.  **Data Constraints:**
    -   **Test Case:** Attempt to persist data that violates a database constraint (e.g., a non-unique email for a `UserModels` entity).
    -   **Purpose:** Verifies that constraints like `unique=true` are correctly enforced at the database level.

---

## 4. Service Layer Testing

### 4.1. Objective

To test the business logic of the application in isolation from the web and data access layers. This is where the core application rules and workflows are validated.

### 4.2. Methodology

-   **Tooling:** Use `@ExtendWith(MockitoExtension.class)` for plain JUnit 5 tests with Mockito.
-   **Scope:** Each test class will focus on a single service.
-   **Isolation:** All external dependencies of the service (e.g., repositories, other services, `PasswordEncoder`) **must be mocked** using `@Mock`. The service under test will be instantiated with `@InjectMocks`.

### 4.3. What to Test

1.  **Successful ("Happy Path") Scenarios:**
    -   **Test Case:** For each public method, test the successful execution path.
    -   **Purpose:** Verifies that the method produces the correct output given valid inputs.
    -   **Example:** For `userService.registerUser()`, verify that the `userRepository.save()` method is called exactly once.
2.  **Business Logic and Edge Cases:**
    -   **Test Case:** Test all conditional logic (if/else statements) and business rule enforcement.
    -   **Purpose:** Ensures the service correctly handles different scenarios.
    -   **Example:** For `userService.registerUser()`, test the case where a user with the given email already exists, and verify that an `IllegalArgumentException` is thrown.
3.  **Exception Handling:**
    -   **Test Case:** Verify that the service throws the expected exceptions when encountering errors (e.g., entity not found).
    -   **Purpose:** Ensures the service behaves predictably in failure scenarios.
    -   **Example:** For a `findById` method, mock the repository to return an empty `Optional` and assert that a `NoSuchElementException` (or a custom exception) is thrown.
4.  **Data Transformation (DTOs):**
    -   **Test Case:** Verify that the service correctly maps between JPA entities and Data Transfer Objects (DTOs).
    -   **Purpose:** Ensures the data contract with the controller layer is upheld.

---

## 5. Controller Layer Testing

### 5.1. Objective

To test the web layer of the application, including request handling, input validation, and serialization/deserialization of DTOs, without launching a full HTTP server.

### 5.2. Methodology

-   **Tooling:** Use the `@WebMvcTest(YourController.class)` annotation. This sets up a Spring context with only the specified controller and the MVC infrastructure.
-   **Scope:** Each test class will focus on a single controller.
-   **Isolation:** All dependencies of the controller (typically services) **must be mocked** using `@MockBean`.
-   **Requests:** Use `MockMvc` to perform and assert expectations on HTTP requests.

### 5.3. What to Test

1.  **Endpoint Accessibility and HTTP Status Codes:**
    -   **Test Case:** For each endpoint, perform a request and verify that the correct HTTP status code is returned for both success and failure scenarios.
    -   **Purpose:** Ensures the endpoints are correctly mapped and respond as expected.
    -   **Example:** A successful `POST /users/register` should return `201 CREATED`. A successful `POST /users/login` should return `200 OK`.
2.  **Request/Response Serialization:**
    -   **Test Case:** Verify that the controller correctly deserializes JSON request bodies into DTOs and serializes response DTOs back into JSON.
    -   **Purpose:** Confirms that the API's data contract is working correctly.
    -   **Example:** Use `ObjectMapper` to create the JSON request body and `jsonPath()` assertions to inspect the response body.
3.  **Input Validation:**
    -   **Test Case:** Send requests with invalid data (e.g., a null email, a short password) and verify that the controller returns a `400 BAD REQUEST` status.
    -   **Purpose:** Ensures that `@Valid` annotations and other validation rules are being enforced.
4.  **Exception Handling:**
    -   **Test Case:** Mock the service layer to throw an exception and verify that the controller's global exception handler (`@ControllerAdvice`) catches it and returns the appropriate HTTP error status.
    -   **Purpose:** Ensures a consistent and predictable error response format for clients.
5.  **New Functionalities and Story Coverage:**
    -   **Test Case:** For each new controller endpoint or modified functionality introduced by recent stories, create dedicated test cases.
    -   **Purpose:** Ensure that all recently implemented features and their corresponding API interactions are thoroughly tested, validating their integration with the service layer and correct response handling. Developers should review the latest stories in `docs/stories/` and the controller implementations to identify all new or modified endpoints requiring test coverage.
