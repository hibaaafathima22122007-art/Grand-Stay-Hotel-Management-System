
package com.hotel;

public class Guest {

    private int guestId;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    public Guest(int guestId, String fullName, String phone,
                 String email, String address) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getGuestId() {
        return guestId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
