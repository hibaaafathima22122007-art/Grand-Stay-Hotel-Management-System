
package com.hotel;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int guestsId;
    private int roomId;
    private String guestName;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String bookingStatus;

    public Booking(int bookingId, int guestsId, int roomId,
                   String guestName, String roomNumber,
                   LocalDate checkIn, LocalDate checkOut,
                   String bookingStatus) {
        this.bookingId = bookingId;
        this.guestsId = guestsId;
        this.roomId = roomId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.bookingStatus = bookingStatus;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getGuestsId() {
        return guestsId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }
}
