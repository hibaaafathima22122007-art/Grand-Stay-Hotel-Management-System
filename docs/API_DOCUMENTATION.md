# API and Route Documentation

## 1. Overview

Grand Stay is a server-rendered Spring MVC web application. Its routes return Thymeleaf pages or redirect the browser to another page. The application does not expose a documented JSON REST API.

Form data is submitted using HTTP GET or POST requests.

## 2. Route Table

| Method | URL | Parameters / Form Fields | Expected Result |
|---|---|---|---|
| GET | `/` | None | Displays the home page |
| GET | `/login` | None | Displays the login page |
| POST | `/login` | `username`, `password` | Redirects to dashboard on success or displays an error |
| GET | `/dashboard` | None | Displays hotel statistics |
| GET | `/rooms` | None | Displays room records |
| POST | `/rooms` | `roomNumber`, `roomType`, `price`, `status` | Adds a room or displays validation feedback |
| GET | `/rooms/edit` | `roomId` | Displays the selected room for editing |
| POST | `/rooms/edit` | `roomId`, `roomNumber`, `roomType`, `price`, `status` | Updates room details |
| POST | `/rooms/delete` | `roomId` | Deletes a room when permitted |
| GET | `/guests` | None | Displays guest records |
| POST | `/guests` | `fullName`, `phone`, optional `email`, optional `address` | Registers a guest |
| POST | `/guests/update` | `guestId`, `fullName`, `phone`, optional `email`, optional `address` | Updates guest details |
| POST | `/guests/delete` | `guestId` | Deletes a guest when permitted |
| GET | `/bookings` | None | Displays booking records |
| POST | `/bookings` | `guestsId`, `roomId` | Creates a booking |
| POST | `/bookings/cancel` | `bookingId` | Cancels a booking |
| GET | `/billing` | None | Displays billing records |
| POST | `/billing/create` | `bookingId` | Creates a bill for a booking |
| POST | `/billing/pay` | `billId` | Updates a bill's payment status |
| GET | `/reports` | None | Displays reports |
| GET | `/reports/export` | None | Redirects to the reports page |
| GET | `/logout` | None | Logs out the administrator |

## 3. Request and Response Behaviour

### Administrator Login

**Request**

- Method: `POST`
- URL: `/login`
- Form fields: `username`, `password`

**Successful result:** The application stores the administrator username in the HTTP session and redirects to `/dashboard`.

**Failed result:** The login page is returned with an error message.

### Add Room

**Request**

- Method: `POST`
- URL: `/rooms`
- Form fields: `roomNumber`, `roomType`, `price`, `status`

**Successful result:** The room is saved to MySQL and the application redirects to the room list.

**Invalid result:** The room page displays validation feedback.

### Register Guest

**Request**

- Method: `POST`
- URL: `/guests`
- Form fields: `fullName`, `phone`, `email`, `address`

Email and address are optional in the controller.

### Create Booking

**Request**

- Method: `POST`
- URL: `/bookings`
- Form fields: `guestsId`, `roomId`

The controller processes the submitted guest and room identifiers and applies the booking logic defined in the application.

### Create Bill

**Request**

- Method: `POST`
- URL: `/billing/create`
- Form field: `bookingId`

The controller processes the booking and creates a bill according to the application's billing logic.

## 4. Authentication

The `LoginInterceptor` checks for the `loggedInAdmin` session attribute. The `WebConfig` class applies the interceptor to dashboard, rooms, guests, bookings, billing, and reports routes.

When an administrator session is not present, a protected request is redirected to `/login`.

## 5. Database Interaction

The `HomeController` uses Spring's `JdbcTemplate` to execute SQL queries and update database records. Form submissions are handled by controller methods, and results are displayed using Thymeleaf templates.

## 6. Verification

Test the routes through the browser interface after starting the application and connecting the MySQL database. Use valid form values and confirm the resulting page, redirect, and database changes.

**Note:** `/reports/export` currently redirects to the reports page; it does not generate a downloadable export file.
