# Grand Stay — Design Documentation

## 1. Problem Statement

Hotel operations involve maintaining room information, guest records, bookings, and payments. Managing these records manually can lead to duplicated information, slower retrieval, and difficulty tracking room availability and outstanding payments.

Grand Stay provides a central web application for an administrator to manage these activities using a MySQL database.

## 2. Objectives

- Centralize hotel room and guest records.
- Support booking creation and cancellation.
- Generate bills and track payment status.
- Display hotel statistics on a dashboard.
- Restrict management pages to authenticated administrator sessions.
- Store operational data in a relational database.

## 3. System Architecture

```text
Administrator
      |
      v
Web Browser
      |
      v
Thymeleaf HTML Pages
      |
      v
Spring MVC Controller
(HomeController)
      |
      v
JdbcTemplate
      |
      v
MySQL Database
(hotel_management)
```

### Architecture Components

**Presentation layer:** Thymeleaf templates and static resources display pages and forms to the administrator.

**Controller layer:** `HomeController` handles HTTP requests, validates selected form values, retrieves records, and chooses the page or redirect.

**Access-control layer:** `LoginInterceptor` checks the administrator session before protected pages are accessed. `WebConfig` registers the interceptor for protected routes.

**Data-access layer:** Spring `JdbcTemplate` executes SQL statements against MySQL.

**Database layer:** The database stores administrator accounts, rooms, guests, bookings, and bills.

## 4. Database Entities

The application uses the following tables. The exact column definitions should match the SQL schema used to create the database.

### Admin

Stores administrator login information.

- Username
- Password

### Rooms

Stores room details.

- `room_id` — room identifier
- `room_number` — room number
- `room_type` — category of room
- `price` — room price
- `status` — availability or maintenance status

### Guests

Stores guest contact details.

- `guests_id` — guest identifier
- `full_name` — guest name
- `phone` — contact number
- `email` — email address
- `address` — postal address

### Bookings

Stores guest reservations.

- `booking_id` — booking identifier
- `guests_id` — guest reference
- `room_id` — room reference
- `check_in` — check-in date
- `check_out` — check-out date
- `booking_status` — booking state

### Bills

Stores booking billing and payment information.

- `bill_id` — bill identifier
- `booking_id` — booking reference
- `total_amount` — total charge
- `payment_status` — payment state
- `bill_date` — bill creation date

## 5. Entity Relationships

```text
Guests  1 -------- many  Bookings
Rooms   1 -------- many  Bookings
Bookings 1 ------- many  Bills (if the database schema permits multiple bills)
Admin   stores administrator login information
```

A guest can have multiple bookings over time. A room can be referenced by multiple booking records over time. Each booking references a guest and a room. Bills reference bookings.

**Important:** Confirm the actual foreign keys and relationship constraints in your MySQL schema. The diagram describes the intended relationships and should be adjusted if the database defines them differently.

## 6. Main Workflows

### Login Workflow

1. The administrator opens the login page.
2. The username and password are submitted.
3. The application checks the administrator record in MySQL.
4. If the credentials match, the session is created and the administrator is redirected to the dashboard.
5. Otherwise, an error message is displayed.

### Room Management Workflow

1. The administrator opens the rooms page.
2. The application retrieves room records from MySQL.
3. The administrator can add, edit, or delete room records.
4. The controller validates the submitted values and performs the corresponding database operation.

### Booking and Billing Workflow

1. The administrator selects a guest and room to create a booking.
2. The application processes the booking request.
3. A bill can be created for a booking.
4. The payment status can be updated through the billing page.

## 7. Validation and Constraints

The controller checks important inputs such as room number, room type, and positive room price. It also checks required guest information and handles certain duplicate or deletion cases.

Database constraints should be used where appropriate to preserve data integrity.

## 8. Security Considerations

- Protected pages use an HTTP session and a login interceptor.
- Database credentials should be supplied through environment variables, not committed to GitHub.
- For production use, passwords should be stored using a secure password-hashing mechanism rather than plaintext comparison.
- Additional protections, including CSRF configuration and stronger input validation, should be reviewed before public deployment.

## 9. Limitations and Future Improvements

- Implement an actual downloadable report export.
- Improve password storage and administrator account management.
- Add stronger validation for booking dates and room availability.
- Add automated tests for controllers and database operations.
- Improve error handling and audit logging.
