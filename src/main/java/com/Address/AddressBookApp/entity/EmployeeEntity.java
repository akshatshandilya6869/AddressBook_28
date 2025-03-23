package com.Address.AddressBookApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    private String name;
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public EmployeeEntity() {}

    public EmployeeEntity(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }
}