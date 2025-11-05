# Task Flow Project Product Requirements Document (PRD)

## 1. Goals and Background Context

### 1.1. Goals
-   Define a well-structured, simple task flow project using Spring Boot.
-   Be ready to start development in two days.
-   Implement core user authentication and project/task creation.
-   Enable basic task management and a Kanban board view.
-   Integrate role-based access control for security and permissions.

### 1.2. Background Context
The "Task Flow Project" aims to provide a robust and user-friendly task management system. The initial brainstorming session identified key themes such as a clear separation of concerns, role-based access control, a user-centric workflow, and real-time insights. This document formalizes these concepts into actionable requirements, focusing on foundational elements to enable rapid development.

The primary users of this system are:
-   **Project Managers:** Responsible for creating projects, defining scope, and managing teams.
-   **Collaborators (Team Members):** The primary users who create and manage tasks within projects.
-   **Administrators:** A system-level role for managing users and system-wide settings.

### 1.3. Change Log

| Date       | Version | Description   | Author    |
| :--------- | :------ | :------------ | :-------- |
| 2025-11-05 | 1.0     | Initial Draft | John (PM) |

---

## 2. Requirements

### 2.1. Functional Requirements
-   **FR1:** Users must be able to register for a new account using an email and password.
-   **FR2:** Registered users must be able to log in to the system.
-   **FR3:** Users who have forgotten their password must be able to initiate a recovery process via email.
-   **FR4:** User sessions must be securely managed usingmJSON Web Tokens (JWT).
-   **FR5:** Project Managers must be able to create new projects (boards) with a name and description.
-   **FR6:** Project Managers must be able to invite other users to a project by their email address.
-   **FR7:** Project Managers must be able to assign roles (e.g., Manager, Editor, Viewer) to project members.
-   **FR8:** Project members must be able to see a list of all members on a project and their roles.
-   **FR9:** Authorized users must be able to create new tasks with a title, assignee, priority, due date, and initial completion percentage.
-   **FR10:** Users must be able to view all tasks for a project on a Kanban-style board with columns representing task status.
-   **FR11:** Users must be able to view and edit the details of a task, including its description.
-   **FR12:** Users must be able to change a task's status by dragging and dropping it between columns on the board.
-   **FR13:** Users must be able to add comments to a task.
-   **FR14:** The system must maintain an activity log for each task, tracking its history of changes.

### 2.2. Non-Functional Requirements
-   **NFR1:** The backend system must be built using the Spring Boot framework.
-   **NFR2:** All authenticated endpoints must be secured using JWT.
-   **NFR3:** The application must use an in-memory H2 database for initial development to ensure rapid setup.
-   **NFR4:** A clear role-based access control (RBAC) system must be implemented to restrict actions based on user roles.

---

## 3. User Interface Design Goals

-   **Overall UX Vision:** A clean, intuitive, and user-centric interface that makes task management straightforward and efficient.
-   **Key Interaction Paradigms:** The primary interaction for workflow management will be dragging and dropping tasks on the Kanban board.
-   **Core Screens and Views:**
    -   Login / Registration Page
    -   Main Dashboard (listing user's projects)
    -   Project Board View (Kanban board)
    -   Task Detail View (modal or dedicated page)
-   **Accessibility:** The application should strive to meet WCAG 2.1 AA standards.
-   **Branding:** No specific branding is required. The focus is on a minimalist and clean design.
-   **Target Device and Platforms:** The application will be a responsive web application, accessible on both desktop and mobile browsers.

---

## 4. Technical Assumptions

-   **Repository Structure:** A single **Monorepo** will be used for the project.
-   **Service Architecture:** The application will be a **Monolith**, which is suitable for this project's initial scope.
-   **Testing Requirements:** The project will require **Unit and Integration tests**.
-   **Additional Technical Assumptions:**
    -   **Backend Framework:** Spring Boot
    -   **Security:** Spring Security
    -   **Database:** H2 (for initial development)
    -   **Build Tool:** Maven or Gradle

---

## 5. Epic List

-   **Epic 1: Foundation & User Management:** Establish the project foundation and implement a complete, secure user authentication and registration system.
-   **Epic 2: Core Project & Task Management:** Implement the creation and management of projects (boards) and tasks.
-   **Epic 3: Collaboration & Workflow:** Implement core task management features, including status changes on the Kanban board, comments, and activity logs.
-   **Epic 4: Reporting & Insights:** Implement basic dashboards for viewing project progress and task distribution.

---

## 6. Epic 1 Details: Foundation & User Management

**Goal:** To set up the foundational structure of the application, including the Spring Boot project itself, and implement a complete, secure user authentication and registration system.

### Story 1.1: Project Setup
**As a** developer,
**I want** to set up a new Spring Boot project with necessary dependencies (Web, Security, JPA, H2),
**so that** we have a foundational codebase to build upon.

**Acceptance Criteria:**
1.  A new Spring Boot project is successfully created.
2.  The `pom.xml` or `build.gradle` includes dependencies for Spring Web, Spring Security, Spring Data JPA, and the H2 database.
3.  The application starts successfully without any errors.

### Story 1.2: User Registration
**As a** new user,
**I want** to register with my email and password,
**so that** I can get an account to access the application.

**Acceptance Criteria:**
1.  An unauthenticated user can send a POST request with an email and password to a `/register` endpoint.
2.  The user's password is securely hashed using a strong algorithm (e.g., bcrypt) before being stored.
3.  A new user record is created in the database.
4.  A success response is returned after the user is created.

### Story 1.3: User Login & JWT Generation
**As a** returning user,
**I want** to log in with my credentials,
**so that** I can receive a JWT to access protected resources.

**Acceptance Criteria:**
1.  A registered user can send a POST request with their email and password to a `/login` endpoint.
2.  If the credentials are valid, the server returns a valid JWT.
3.  If the credentials are invalid, the server returns an authentication error.
4.  The returned JWT can be successfully used to authenticate against a protected test endpoint.

---

## 7. Next Steps

This document will now be used to guide the architecture and development phases. The next steps are:
1.  Review and approval of this PRD by stakeholders.
2.  Creation of the technical architecture document by the Architect.
3.  Begin development starting with Epic 1.
