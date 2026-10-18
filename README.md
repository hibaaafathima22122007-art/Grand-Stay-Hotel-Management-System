# Grand Stay — Hotel Management System

## 1. Project Overview

Grand Stay is a web-based Hotel Management System developed to simplify hotel operations. It allows an administrator to manage rooms, guest details, bookings, billing, and reports through a web interface.

## 2. Technologies Used

- Java
- Spring Boot
- Spring MVC
- Thymeleaf
- JDBC and JdbcTemplate
- MySQL
- Maven
- HTML and CSS
- IntelliJ IDEA
- Git and GitHub

## 3. Main Features

- Admin login and session-based access control
- Dashboard displaying hotel statistics
- Room management: view, add, edit, and delete rooms
- Guest management: register, view, update, and delete guests
- Booking management and booking cancellation
- Bill generation and payment status updates
- Reports page
- MySQL database integration

## 4. Prerequisites

Install the following software before running the project:

- JDK compatible with the project's Spring Boot and Maven configuration
- IntelliJ IDEA
- MySQL Server
- Maven (or use the Maven wrapper if provided)

## 5. Database Configuration

1. Start the MySQL server.
2. Create a database named `hotel_management`.
3. Create the required tables: `admin`, `rooms`, `guests`, `bookings`, and `bills`.
4. Insert an administrator account into the `admin` table.

Configure the database connection in `src/main/resources/application.properties`.

The project uses the following settings:

- Database URL: `jdbc:mysql://localhost:3306/hotel_management`
- Database username: `root`
- Database password: supplied through the `DB_PASSWORD` environment variable

Set `DB_PASSWORD` to your own MySQL password in the IntelliJ Run Configuration. Do not commit actual passwords to GitHub.

**Important:** The database and tables must be created before running the application. Use the SQL schema that matches the project's actual queries and model fields.

## 6. How to Run

1. Open the project in IntelliJ IDEA.
2. Make sure MySQL is running.
3. Configure the `DB_PASSWORD` environment variable in the run configuration.
4. Reload the Maven project and allow dependencies to download.
5. Run `HotelManagementWebApplication.java`.
6. Open `http://localhost:8080/login` in your browser.
7. Sign in using an administrator account already stored in the database.

## 7. Application Pages

| Page | Purpose |
|---|---|
| Login | Administrator authentication |
| Dashboard | Overview of rooms, guests, bookings, and billing |
| Rooms | Manage room details |
| Guests | Manage guest records |
| Bookings | Create and cancel bookings |
| Billing | Create bills and update payment status |
| Reports | View hotel reports |

## 8. Testing Checklist

- Verify that a valid administrator can log in.
- Verify that invalid login credentials are rejected.
- Add a room and confirm it appears in the room list.
- Register a guest and confirm the details are displayed.
- Create and cancel a booking.
- Generate a bill and update its payment status.
- Verify that dashboard statistics reflect the database records.

## 9. Project Structure

```text
HotelManagementSystem2/
├── src/
│   └── main/
│       ├── java/com/hotel/
│       └── resources/
│           ├── templates/
│           ├── static/
│           └── application.properties
├── docs/
│   ├── API_DOCUMENTATION.md
│   └── DESIGN_DOCUMENTATION.md
├── pom.xml
└── README.md
```

## 10. Repository

The project source code and documentation are maintained in GitHub.

Repository: https://github.com/hibaafathima22122007-art/Grand-Stay-Hotel-Management-System
