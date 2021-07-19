package com.inik.neonadeuri.models;

import java.io.Serializable;

public class RoomData implements Serializable {
    private String username;
    private String roomNumber;
    private String receiver;

    public RoomData(String username, String roomNumber,String receiver) {
        this.username = username;
        this.roomNumber = roomNumber;
        this.receiver = receiver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
