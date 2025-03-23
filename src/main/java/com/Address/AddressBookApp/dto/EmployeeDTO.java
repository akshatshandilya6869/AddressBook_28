package com.Address.AddressBookApp.dto;

public class EmployeeDTO {
    private String name;
    private String email;
    private Long id;

    // Default Constructor
    public EmployeeDTO() {}

    // Parameterized Constructor
    public EmployeeDTO(String name, String email, Long id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}