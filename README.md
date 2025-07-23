
# Banking Portal

This is a comprehensive Banking Portal application built with Spring Boot, providing a secure and efficient platform for users to manage their bank accounts. The application includes functionalities for user registration, login, account creation, PIN management, and fund transfers.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Database Schema](#database-schema)

## Features

-   **User Management:**
    -   User registration with username, email, and password.
    -   Secure user login with JWT-based authentication.
-   **Account Management:**
    -   Create multiple bank accounts (Savings or Current).
    -   View account details and balance.
-   **PIN Management:**
    -   Create a 4-digit PIN for account transactions.
    -   Update the PIN securely.
-   **Transactions:**
    -   Deposit funds into an account.
    -   Withdraw funds from an account.
    -   Transfer funds between accounts.

## Technologies Used

-   **Backend:**
    -   Java 21
    -   Spring Boot 3.5.3
    -   Spring Security
    -   Spring Data JPA
-   **Database:**
    -   MySQL
-   **Authentication:**
    -   JSON Web Tokens (JWT)
-   **Build Tool:**
    -   Maven

## Getting Started

### Prerequisites

-   Java 21
-   Maven
-   MySQL

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Falasefemi2/banking-portal.git
    cd banking-portal
    ```

2.  **Create a `.env` file** in the root directory and add the following environment variables:
    ```
    DB_URL=jdbc:mysql://localhost:3306/your_database_name
    DB_USERNAME=your_database_username
    DB_PASSWORD=your_database_password
    JWT_SECRET=your_jwt_secret
    ```

3.  **Build the project:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

The application will be running on `http://localhost:8080`.

## API Endpoints

### User Endpoints

-   `POST /api/users/register`: Register a new user.
-   `POST /api/users/login`: Login a user and get a JWT token.

### Account Endpoints

-   `POST /api/accounts/register`: Create a new bank account.
-   `POST /api/accounts/create-pin`: Create a PIN for an account.
-   `PUT /api/accounts/update-pin`: Update an existing PIN.
-   `GET /api/accounts/pin-status/{accountNumber}`: Check if a PIN is created for an account.

### Transaction Endpoints

-   `POST /api/transfers/deposit`: Deposit funds into an account.
-   `POST /api/transfers/withdraw`: Withdraw funds from an account.
-   `POST /api/transfers/fund-transfer`: Transfer funds between accounts.

## Security

-   **Authentication:** The application uses JWT for securing the API endpoints. The token is generated upon successful login and must be included in the `Authorization` header for all protected requests.
-   **Authorization:** Role-based access control is implemented using Spring Security. The `USER` role is required to access account and transaction-related endpoints.
-   **Password Encryption:** User passwords and PINs are encrypted using BCrypt before being stored in the database.

## Database Schema

The application uses the following database schema:

-   **users:** Stores user information.
-   **roles:** Stores user roles (e.g., `ROLE_USER`, `ROLE_ADMIN`).
-   **user_roles:** A join table for the many-to-many relationship between users and roles.
-   **accounts:** Stores bank account information.
-   **transactions:** Stores all financial transactions.
