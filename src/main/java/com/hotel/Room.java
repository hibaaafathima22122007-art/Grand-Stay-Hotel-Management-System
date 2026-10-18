
package com.hotel;

public class Room {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double price;
    private String status;

    public Room(int roomId, String roomNumber,
                String roomType, double price, String status) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}
