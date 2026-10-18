
package com.hotel;
import java.util.ArrayList;
import java.util.List;
//import javax.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final JdbcTemplate jdbcTemplate;

    // Constructor
    public HomeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Display login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Check admin login using MySQL
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        String sql = "SELECT COUNT(*) FROM admin "
                + "WHERE username = ? AND password = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class, username, password);

        if (count != null && count > 0) {
            session.setAttribute("loggedInAdmin", username);
            session.setMaxInactiveInterval(30 * 60);
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid username or password!");
        return "login";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Room statistics
        model.addAttribute("totalRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms",
                        Integer.class));

        model.addAttribute("availableRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms WHERE status = 'Available'",
                        Integer.class));

        model.addAttribute("occupiedRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms WHERE status = 'Occupied'",
                        Integer.class));

        model.addAttribute("maintenanceRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms WHERE status = 'Maintenance'",
                        Integer.class));

        // Guest statistics
        model.addAttribute("totalGuests",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM guests",
                        Integer.class));

        // Booking statistics
        model.addAttribute("totalBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings",
                        Integer.class));

        model.addAttribute("confirmedBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings WHERE booking_status = 'Confirmed'",
                        Integer.class));

        model.addAttribute("cancelledBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings WHERE booking_status = 'Cancelled'",
                        Integer.class));

        // Billing statistics
        model.addAttribute("totalBills",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bills",
                        Integer.class));

        model.addAttribute("pendingPayments",
                jdbcTemplate.queryForObject(
                        "SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE payment_status = 'Pending'",
                        java.math.BigDecimal.class));

        return "dashboard";
    }


    // Display all rooms
    @GetMapping("/rooms")
    public String rooms(Model model) {

        String sql = "SELECT room_id, room_number, room_type, "
                + "price, status FROM rooms "
                + "ORDER BY room_number";

        List<Room> roomList = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                )
        );

        model.addAttribute("rooms", roomList);

        return "rooms";
    }

    // Add a new room
    @PostMapping("/rooms")
    public String addRoom(
            @RequestParam String roomNumber,
            @RequestParam String roomType,
            @RequestParam double price,
            @RequestParam String status,
            Model model) {

        // Validate room details
        if (roomNumber == null || roomNumber.trim().isEmpty()
                || roomType == null || roomType.trim().isEmpty()
                || price <= 0) {

            model.addAttribute(
                    "error",
                    "Enter a valid room number, room type, and price."
            );

            return rooms(model);
        }

        try {
            String sql = "INSERT INTO rooms "
                    + "(room_number, room_type, price, status) "
                    + "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(
                    sql,
                    roomNumber.trim(),
                    roomType.trim(),
                    price,
                    status
            );

            return "redirect:/rooms";

        } catch (DuplicateKeyException ex) {

            model.addAttribute(
                    "error",
                    "That room number already exists."
            );

            return rooms(model);
        }
    }

    // Delete a room

    @PostMapping("/rooms/delete")
    public String deleteRoom(
            @RequestParam int roomId,
            RedirectAttributes redirectAttributes) {

        // Check whether this room has booking records
        Integer bookingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE room_id = ?",
                Integer.class,
                roomId
        );

        if (bookingCount != null && bookingCount > 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "This room has booking records and cannot be deleted."
            );

            return "redirect:/rooms";
        }

        // Delete only if no bookings reference this room
        int deleted = jdbcTemplate.update(
                "DELETE FROM rooms WHERE room_id = ?",
                roomId
        );

        if (deleted > 0) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Room deleted successfully!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Room not found."
            );
        }

        return "redirect:/rooms";
    }


    // Open the Edit Room page
    @GetMapping("/rooms/edit")
    public String editRoomPage(
            @RequestParam int roomId,
            Model model) {

        String sql = "SELECT room_id, room_number, room_type, "
                + "price, status FROM rooms WHERE room_id = ?";

        List<Room> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                ),
                roomId
        );

        if (results.isEmpty()) {
            return "redirect:/rooms";
        }

        model.addAttribute("room", results.get(0));
        return "edit-room";
    }

    // Save updated room details
    @PostMapping("/rooms/edit")
    public String updateRoom(
            @RequestParam int roomId,
            @RequestParam String roomNumber,
            @RequestParam String roomType,
            @RequestParam double price,
            @RequestParam String status,
            Model model) {

        if (roomNumber == null || roomNumber.trim().isEmpty()
                || roomType == null || roomType.trim().isEmpty()
                || price <= 0) {

            model.addAttribute("error", "Enter valid room details.");
            return editRoomPage(roomId, model);
        }

        try {
            String sql = "UPDATE rooms SET room_number = ?, "
                    + "room_type = ?, price = ?, status = ? "
                    + "WHERE room_id = ?";

            int updated = jdbcTemplate.update(
                    sql,
                    roomNumber.trim(),
                    roomType,
                    price,
                    status,
                    roomId
            );

            if (updated == 0) {
                return "redirect:/rooms";
            }

            return "redirect:/rooms";

        } catch (DuplicateKeyException ex) {
            model.addAttribute(
                    "error",
                    "That room number already exists."
            );
            return editRoomPage(roomId, model);
        }
    }

    @GetMapping("/guests")
    public String showGuests(Model model) {

        String sql = "SELECT guests_id, full_name, phone, email, address " +
                "FROM guests ORDER BY guests_id DESC";

        List<Guest> guests = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Guest(
                        rs.getInt("guests_id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                )
        );

        model.addAttribute("guests", guests);
        return "guests";
    }

    @PostMapping("/guests/update")
    public String updateGuest(
            @RequestParam int guestId,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {

        if (fullName.trim().isEmpty() || phone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Full name and phone number are required.");
            return "redirect:/guests";
        }

        String sql =
                "UPDATE guests SET full_name = ?, phone = ?, email = ?, address = ? " +
                        "WHERE guests_id = ?";

        int updated = jdbcTemplate.update(
                sql,
                fullName.trim(),
                phone.trim(),
                email == null || email.trim().isEmpty() ? null : email.trim(),
                address == null || address.trim().isEmpty() ? null : address.trim(),
                guestId
        );

        if (updated > 0) {
            redirectAttributes.addFlashAttribute(
                    "success", "Guest details updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Guest not found or no details were changed.");
        }

        return "redirect:/guests";
    }


    @PostMapping("/guests/delete")
    public String deleteGuest(
            @RequestParam int guestId,
            RedirectAttributes redirectAttributes) {

        Integer bookingCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE guests_id = ?",
                Integer.class,
                guestId
        );

        if (bookingCount != null && bookingCount > 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "This guest has booking records and cannot be deleted.");
            return "redirect:/guests";
        }

        int deleted = jdbcTemplate.update(
                "DELETE FROM guests WHERE guests_id = ?",
                guestId
        );

        if (deleted > 0) {
            redirectAttributes.addFlashAttribute(
                    "success", "Guest deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Guest not found.");
        }

        return "redirect:/guests";
    }




    @PostMapping("/guests")
    public String addGuest(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            Model model) {

        if (fullName.trim().isEmpty() || phone.trim().isEmpty()) {
            model.addAttribute("error", "Name and phone are required.");
            return showGuests(model);
        }

        String sql = "INSERT INTO guests (full_name, phone, email, address) " +
                "VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                fullName.trim(),
                phone.trim(),
                email == null || email.trim().isEmpty() ? null : email.trim(),
                address == null || address.trim().isEmpty() ? null : address.trim()
        );

        return "redirect:/guests";
    }



    @GetMapping("/bookings")
    public String showBookings(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut,

            Model model) {

        // 1. Load registered guests
        String guestSql =
                "SELECT guests_id, full_name, phone, email, address " +
                        "FROM guests ORDER BY full_name";

        List<Guest> guests = jdbcTemplate.query(guestSql, (rs, rowNum) ->
                new Guest(
                        rs.getInt("guests_id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                )
        );

        // 2. Load rooms available for the selected date range
        String roomSql =
                "SELECT r.room_id, r.room_number, r.room_type, r.price, r.status " +
                        "FROM rooms r " +
                        "WHERE r.status = 'Available' ";

        List<Object> roomParams = new ArrayList<>();

        if (checkIn != null && checkOut != null
                && checkOut.isAfter(checkIn)) {

            roomSql +=
                    "AND NOT EXISTS (" +
                            "SELECT 1 FROM bookings b " +
                            "WHERE b.room_id = r.room_id " +
                            "AND b.booking_status <> 'Cancelled' " +
                            "AND b.check_in < ? AND b.check_out > ?" +
                            ") ";

            roomParams.add(java.sql.Date.valueOf(checkOut));
            roomParams.add(java.sql.Date.valueOf(checkIn));
        }

        roomSql += "ORDER BY r.room_number";

        List<Room> rooms = jdbcTemplate.query(
                roomSql,
                (rs, rowNum) -> new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status")
                ),
                roomParams.toArray()
        );

        // 3. Load existing bookings
        String bookingSql =
                "SELECT b.booking_id, b.guests_id, b.room_id, " +
                        "g.full_name, r.room_number, b.check_in, b.check_out, " +
                        "b.booking_status " +
                        "FROM bookings b " +
                        "JOIN guests g ON b.guests_id = g.guests_id " +
                        "JOIN rooms r ON b.room_id = r.room_id " +
                        "ORDER BY b.booking_id DESC";

        List<Booking> bookings = jdbcTemplate.query(
                bookingSql, (rs, rowNum) ->
                        new Booking(
                                rs.getInt("booking_id"),
                                rs.getInt("guests_id"),
                                rs.getInt("room_id"),
                                rs.getString("full_name"),
                                rs.getString("room_number"),
                                rs.getDate("check_in").toLocalDate(),
                                rs.getDate("check_out").toLocalDate(),
                                rs.getString("booking_status")
                        )
        );

        // 4. Send data to the webpage
        model.addAttribute("guests", guests);
        model.addAttribute("rooms", rooms);
        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedCheckIn", checkIn);
        model.addAttribute("selectedCheckOut", checkOut);

        return "bookings";
    }

    // Keeps existing calls such as showBookings(model) working
    public String showBookings(Model model) {
        return showBookings(null, null, model);
    }


    @PostMapping("/bookings")
    public String createBooking(
            @RequestParam int guestsId,
            @RequestParam int roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Validate the dates

        if (checkIn.isBefore(LocalDate.now())) {
            model.addAttribute("error",
                    "Check-in date cannot be in the past.");

            model.addAttribute("selectedCheckIn", checkIn);
            model.addAttribute("selectedCheckOut", checkOut);

            return showBookings(model);
        }
        if (!checkOut.isAfter(checkIn)) {
            model.addAttribute("error",
                    "Check-out date must be after check-in date.");

            return showBookings(model);
        }
        // 2. Check whether the room is already booked
        String conflictSql =
                "SELECT COUNT(*) FROM bookings " +
                        "WHERE room_id = ? " +
                        "AND booking_status <> 'Cancelled' " +
                        "AND check_in < ? AND check_out > ?";

        Integer conflicts = jdbcTemplate.queryForObject(
                conflictSql,
                Integer.class,
                roomId,
                Date.valueOf(checkOut),
                Date.valueOf(checkIn)
        );

        if (conflicts != null && conflicts > 0) {
            model.addAttribute("error",
                    "This room is already booked for those dates.");
            model.addAttribute("selectedCheckIn", checkIn);
            model.addAttribute("selectedCheckOut", checkOut);
            return showBookings(model);
            //return showBookings(model);
        }

        // 3. Save the booking in MySQL
        String insertSql =
                "INSERT INTO bookings " +
                        "(guests_id, room_id, check_in, check_out, booking_status) " +
                        "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                insertSql,
                guestsId,
                roomId,
                Date.valueOf(checkIn),
                Date.valueOf(checkOut),
                "Confirmed"
        );

        // 4. Return to the bookings page
        redirectAttributes.addFlashAttribute(
                "success", "Booking created successfully!");

        return "redirect:/bookings";
    }

    @PostMapping("/bookings/cancel")
    public String cancelBooking(
            @RequestParam int bookingId,
            RedirectAttributes redirectAttributes) {

        String sql =
                "UPDATE bookings " +
                        "SET booking_status = 'Cancelled' " +
                        "WHERE booking_id = ? " +
                        "AND booking_status <> 'Cancelled'";

        int updated = jdbcTemplate.update(sql, bookingId);

        if (updated > 0) {
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Booking cancelled successfully!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Booking not found or already cancelled."
            );
        }

        return "redirect:/bookings";
    }



    @GetMapping("/billing")
    public String showBilling(Model model) {

        String bookingSql =
                "SELECT b.booking_id, g.full_name, r.room_number, " +
                        "DATEDIFF(b.check_out, b.check_in) AS nights, r.price " +
                        "FROM bookings b " +
                        "JOIN guests g ON b.guests_id = g.guests_id " +
                        "JOIN rooms r ON b.room_id = r.room_id " +
                        "WHERE b.booking_status <> 'Cancelled' " +
                        "AND NOT EXISTS (SELECT 1 FROM bills bi " +
                        "WHERE bi.booking_id = b.booking_id) " +
                        "ORDER BY b.booking_id DESC";

        model.addAttribute("eligibleBookings",
                jdbcTemplate.queryForList(bookingSql));

        String billSql =
                "SELECT bi.bill_id, bi.booking_id, g.full_name AS guest_name, " +
                        "r.room_number, DATEDIFF(b.check_out, b.check_in) AS nights, " +
                        "bi.total_amount, bi.payment_status, bi.bill_date " +
                        "FROM bills bi " +
                        "JOIN bookings b ON bi.booking_id = b.booking_id " +
                        "JOIN guests g ON b.guests_id = g.guests_id " +
                        "JOIN rooms r ON b.room_id = r.room_id " +
                        "ORDER BY bi.bill_id DESC";

        model.addAttribute("bills", jdbcTemplate.query(billSql,
                (rs, rowNum) -> new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("booking_id"),
                        rs.getString("guest_name"),
                        rs.getString("room_number"),
                        rs.getInt("nights"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("payment_status"),
                        rs.getDate("bill_date").toLocalDate()
                )));

        return "billing";
    }


    @PostMapping("/billing/create")
    public String createBill(
            @RequestParam int bookingId,
            RedirectAttributes redirectAttributes) {

        String checkSql =
                "SELECT COUNT(*) FROM bookings " +
                        "WHERE booking_id = ? AND booking_status <> 'Cancelled'";

        Integer validBooking = jdbcTemplate.queryForObject(
                checkSql, Integer.class, bookingId);

        if (validBooking == null || validBooking == 0) {
            redirectAttributes.addFlashAttribute(
                    "error", "Booking not found or cancelled.");
            return "redirect:/billing";
        }

        String duplicateSql =
                "SELECT COUNT(*) FROM bills WHERE booking_id = ?";

        Integer existingBill = jdbcTemplate.queryForObject(
                duplicateSql, Integer.class, bookingId);

        if (existingBill != null && existingBill > 0) {
            redirectAttributes.addFlashAttribute(
                    "error", "A bill already exists for this booking.");
            return "redirect:/billing";
        }

        String amountSql =
                "SELECT DATEDIFF(b.check_out, b.check_in) AS nights, r.price " +
                        "FROM bookings b JOIN rooms r ON b.room_id = r.room_id " +
                        "WHERE b.booking_id = ?";

        Map<String, Object> details =
                jdbcTemplate.queryForMap(amountSql, bookingId);

        int nights = ((Number) details.get("nights")).intValue();
        BigDecimal price = new BigDecimal(details.get("price").toString());
        BigDecimal total = price.multiply(BigDecimal.valueOf(nights));

        String insertSql =
                "INSERT INTO bills " +
                        "(booking_id, total_amount, payment_status, bill_date) " +
                        "VALUES (?, ?, 'Pending', ?)";

        jdbcTemplate.update(insertSql, bookingId, total,
                java.sql.Date.valueOf(LocalDate.now()));

        redirectAttributes.addFlashAttribute(
                "success", "Bill generated successfully!");

        return "redirect:/billing";
    }

    @GetMapping("/reports")
    public String showReports(Model model) {

        // Room statistics
        model.addAttribute("totalRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms", Integer.class));

        model.addAttribute("availableRooms",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM rooms WHERE status = 'Available'",
                        Integer.class));

        // Guest statistics
        model.addAttribute("totalGuests",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM guests", Integer.class));

        // Booking statistics
        model.addAttribute("totalBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings", Integer.class));

        model.addAttribute("confirmedBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings WHERE booking_status = 'Confirmed'",
                        Integer.class));

        model.addAttribute("cancelledBookings",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bookings WHERE booking_status = 'Cancelled'",
                        Integer.class));

        // Billing statistics
        model.addAttribute("totalBills",
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM bills", Integer.class));

        model.addAttribute("pendingPayments",
                jdbcTemplate.queryForObject(
                        "SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE payment_status = 'Pending'",
                        BigDecimal.class));

        model.addAttribute("totalBilled",
                jdbcTemplate.queryForObject(
                        "SELECT COALESCE(SUM(total_amount), 0) FROM bills",
                        BigDecimal.class));

        return "reports";
    }


    @GetMapping("/reports/export")
    public String exportReports() {
        return "redirect:/reports";
    }

    @PostMapping("/billing/pay")
    public String markBillAsPaid(
            @RequestParam int billId,
            RedirectAttributes redirectAttributes) {

        String sql =
                "UPDATE bills " +
                        "SET payment_status = 'Paid' " +
                        "WHERE bill_id = ? AND payment_status = 'Pending'";

        int updated = jdbcTemplate.update(sql, billId);

        if (updated > 0) {
            redirectAttributes.addFlashAttribute(
                    "success", "Payment recorded successfully!");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Bill not found or already paid.");
        }

        return "redirect:/billing";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }





}
