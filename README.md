# Task Management API with Authentication and Reporting

A secure, production-ready RESTful API built with **Java 17** and **Spring Boot** for managing user-specific tasks, featuring **Stateless JWT Authentication**, **Role-Based Access Control (RBAC)**, **Dashboard Reporting**, and **Centralized Exception Handling**.

---

## Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot, Spring Web, Spring Security
* **Persistence:** Spring Data JPA (Hibernate)
* **Database:** PostgreSQL
* **Authentication:** JSON Web Token (`jjwt` 0.12.6), BCrypt Password Hashing
* **Validation:** Jakarta Bean Validation (`@Valid`)
* **Build Tool:** Maven

---

## System Architecture & Features
1. **JWT Authentication & Role-Based Access Control (RBAC):**
   * Stateless authentication via `OncePerRequestFilter`.
   * Supports two roles: `ROLE_USER` and `ROLE_ADMIN`.
   * Strict task ownership enforcement: Users can only view, update, or delete their own tasks, while Admins have system-wide visibility.
2. **Relational Data Modeling (JPA):**
   * `User` (`1`) $\leftrightarrow$ `Task` (`N`) bidirectional mapping with `FetchType.LAZY` and cascade rules.
   * Automated audit timestamps (`createdAt`, `updatedAt`).
3. **Dashboard Analytics & Reporting:**
   * Custom JPQL and derived count queries calculating task breakdowns by `Status` (`PENDING`, `IN_PROGRESS`, `COMPLETED`), `Priority` (`LOW`, `MEDIUM`, `HIGH`), overdue task detection, and completion rate percentages.
4. **Input Validation & Global Error Handling:**
   * Request DTO validation (`@NotBlank`, `@Email`, `@Size`, `@FutureOrPresent`).
   * `@RestControllerAdvice` (`GlobalExceptionHandler`) returning standardized JSON error payloads for `400`, `401`, `403`, `404`, and `500` HTTP status codes.

---

## REST API Endpoints

### 1. Authentication (`/api/auth`) — *Public*
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user (`ROLE_USER` or `ROLE_ADMIN`) and receive a JWT token |
| `POST` | `/api/auth/login` | Authenticate with email & password and receive a JWT token |

### 2. Task Management (`/api/tasks`) — *Requires `ROLE_USER` or `ROLE_ADMIN`*
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/tasks` | Create a new task for the logged-in user |
| `GET` | `/api/tasks` | Fetch all tasks for the logged-in user (Optional query params: `?status=PENDING` or `?priority=HIGH`) |
| `GET` | `/api/tasks/{id}` | Fetch task details by ID (Ownership verified) |
| `PUT` | `/api/tasks/{id}` | Update an existing task (Ownership verified) |
| `DELETE` | `/api/tasks/{id}` | Delete a task by ID (Ownership verified) |

### 3. User Dashboard Reporting (`/api/reports`) — *Requires `ROLE_USER` or `ROLE_ADMIN`*
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/reports/dashboard` | Get logged-in user's task summary (counts by status, priority, overdue tasks, completion rate %) |

### 4. Admin Operations (`/api/admin`) — *Requires `ROLE_ADMIN` Only*
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/admin/tasks` | View all tasks across all users in the system |
| `GET` | `/api/admin/reports/dashboard` | View system-wide analytics (total users, total tasks, status breakdown, overdue tasks, overall completion rate) |

---

## Local Setup & Installation

### Prerequisites
* Java 17+
* Maven
* PostgreSQL

### Steps to Run
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/makwana-darshan/task-management-api.git](https://github.com/makwana-darshan/task-management-api.git)
   cd task-management-api