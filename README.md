# Task Flow

Task Flow is a comprehensive task management application designed to streamline project workflows. It features a robust backend powered by Spring Boot and a modern, responsive frontend built with React. This application allows users to manage projects, tasks, and teams in a collaborative environment, visualized through a Kanban-style board.

## Features

- **User Authentication:** Secure user registration and login with JWT-based authentication.
- **Project Management:** Create and manage projects (boards), invite members, and assign roles.
- **Task Management:** Create, update, and assign tasks with details like priority, due date, and description.
- **Kanban Board:** Visualize and manage tasks in a workflow using a drag-and-drop Kanban board.
- **Collaboration:** Add comments to tasks and view a complete activity log for each task.
- **Role-Based Access Control (RBAC):** Granular permissions for different user roles (e.g., Manager, Editor, Viewer).

## Tech Stack

### Backend

- **Java 21**
- **Spring Boot 3.2.5**
- **Spring Security (with JWT)**
- **Spring Data JPA**
- **Maven**
- **MySQL (for production)**
- **H2 Database (for development)**

### Frontend

- **React**
- **Vite**
- **TypeScript**
- **Tailwind CSS**
- **Shadcn UI**
- **Axios**
- **React Router**

## Getting Started

### Prerequisites

- **Java 21** or later
- **Maven**
- **Node.js** and **npm**

### Installation and Running the Application

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/Thiaghoul/Accenture-final-project.git
    cd Accenture-final-project
    ```

2.  **Run the Backend:**

    Navigate to the `taskFlow` directory and run the Spring Boot application using Maven:

    ```bash
    cd taskFlow
    mvn spring-boot:run
    ```

    The backend will start on `http://localhost:8080`. By default, it uses an in-memory H2 database.

3.  **Run the Frontend:**

    In a new terminal, navigate to the `FrontTaskFlow5` directory, install the dependencies, and start the development server:

    ```bash
    cd ../FrontTaskFlow5
    npm install
    npm run dev
    ```

    The frontend will be available at `http://localhost:5173`.

## API Documentation

The backend API is documented using OpenAPI (Swagger). Once the backend is running, you can access the Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)