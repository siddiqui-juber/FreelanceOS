# FreelanceOS — Backend API
FreelanceOS is an all-in-one backend system for freelancers and small agencies that unifies client management, project tracking, invoicing, and financial visibility into a single platform.


## Project Overview
Freelancers today rely on multiple disconnected tools (CRM, project trackers, invoicing tools). FreelanceOS solves this by providing a single unified backend system.

## Core Objective
- Manage clients, projects, invoices, and finances
- Reduce tool fragmentation
- Provide clear business insights
  
## Modules Implemented
### 1. Authentication & User Management
- User registration & login (JWT-based)
- Access + refresh token system
- Secure password storage (BCrypt)
- Profile management
### 2. Client Management
- Create / update / delete clients
- Email uniqueness per user
- Communication log tracking
- Client lifecycle (Prospect → Active → Past)
### 3. Project Management
- Project creation with auto stage generation
- Task, stage, deliverable, and expense tracking
-Financial summary (profit, expenses, revenue)
- Status lifecycle (Not Started → Completed)
### 4. Invoicing Module
- Draft → Sent → Paid lifecycle
- Line items with dynamic calculations
- Tax & discount handling
- Payment tracking (partial/full)
- Overdue invoice detection

## Tech Stack
| Layer      | Technology            |
| ---------- | --------------------- |
| Language   | Java 21               |
| Framework  | Spring Boot           |
| Database   | MySQL                 |
| ORM        | Hibernate / JPA       |
| Security   | Spring Security + JWT |
| Build Tool | Maven                 |
| API Docs   | Swagger / OpenAPI     |
| Testing    | JUnit + Postman       |

## Prerequisites

### Make sure you have installed:
- Java 21+
- Maven 3.8+
- MySQL 8+
- Git
##  MySQL Setup
1. Open MySQL and create database:

```sql
CREATE DATABASE freelanceos;
```
2 .Update credentials in application.properties
- Configuration (application.properties)
```
spring.datasource.url=jdbc:mysql://localhost:3306/freelanceos
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```
## How to Run the Application
- Step 1:Clone the repository
```
git clone https://github.com/siddiqui-juber/FreelanceOS.git
cd FreelanceOS
```
- Step 2: Build project
```
mvn clean install
```
- Step 3: Run application
```
mvn spring-boot:run
```
## API Documentation (Swagger)
- After running the app, open:
```
 http://localhost:8080/swagger-ui/index.html
```
- Swagger provides:
- All endpoints
- Request/response schemas
- Error codes

## Authentication
- All endpoints require JWT token
- Add header:
```
Authorization: Bearer <your-token>
```
## API Structure
- Base URL:
```
/api/v1/
```
| Module   | Endpoint             |
| -------- | -------------------- |
| Auth     | `/api/v1/auth/login` |
| Clients  | `/api/v1/clients`    |
| Projects | `/api/v1/projects`   |
| Invoices | `/api/v1/invoices`   |

### Business Rules Highlights
- Client email must be unique per user
- Client cannot be deleted if:
- Active projects exist
- Unpaid invoices exist
#### Project:
- Must belong to a client
- Cannot be deleted if invoices exist
#### Invoice:
- Must have at least one line item
- Cannot be modified after leaving Draft
#### Payment:
- Auto updates invoice to PAID

## Testing 
- Run Unit Tests
```
mvn test
```
#### Test Coverage Includes:
- Invoice calculations
- Project financial logic
- Client constraints
- Status transitions

## Postman Collection
- Location in repo:
```
/postman/FreelanceOS_API.json
```
### Postman Coverage

#### The collection includes:

#### All Modules
- Auth
- Client
- Project
- Invoice
#### Each Endpoint Includes
-Headers (Authorization)
-Request body
- Sample response
#### Negative Test Cases
- Duplicate email
- Invalid token
- Missing fields
- Business rule violations
- Unauthorized access
## Architecture
```
Controller → Service → Repository → Database
```
- Controllers: Handle HTTP requests
- Services: Business logic
- Repositories: Data access
## Entity Relationships
- User → Clients → Projects → Stages → Tasks
- Project → Deliverables → Expenses
- Client → Invoices → Payments
## API Standards

- RESTful design  
- JSON responses  

- Proper HTTP status codes:
  - 200 OK  
  - 201 Created  
  - 204 No Content  
  - 400 Bad Request  
  - 401 Unauthorized  
  - 404 Not Found  
  - 409 Conflict  
  - 422 Business Rule Violation
## Scope  
Included


- Backend API
- JWT Authentication
- Full CRUD for all modules
## Final Note


FreelanceOS is designed with scalability, clean architecture, and real-world business logic in mind. This backend serves as the foundation for future frontend and advanced analytics features.
