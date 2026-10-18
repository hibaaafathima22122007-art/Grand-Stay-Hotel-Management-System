
package com.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bill {
    private int billId;
    private int bookingId;
    private String guestName;
    private String roomNumber;
    private int nights;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDate billDate;

    public Bill(int billId, int bookingId, String guestName,
                String roomNumber, int nights,
                BigDecimal totalAmount, String paymentStatus,
                LocalDate billDate) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.nights = nights;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.billDate = billDate;
    }

    public int getBillId() { return billId; }
    public int getBookingId() { return bookingId; }
    public String getGuestName() { return guestName; }
    public String getRoomNumber() { return roomNumber; }
    public int getNights() { return nights; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDate getBillDate() { return billDate; }
}
