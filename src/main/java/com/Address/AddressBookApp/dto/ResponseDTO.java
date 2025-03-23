package com.Address.AddressBookApp.dto;

public class ResponseDTO {
    private String message;
    private String status;

    // Default Constructor
    public ResponseDTO() {}

    // Parameterized Constructor
    public ResponseDTO(String message, String status) {
        this.message = message;
        this.status = status;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
