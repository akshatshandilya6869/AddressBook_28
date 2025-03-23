package com.Address.AddressBookApp.repository;

import com.Address.AddressBookApp.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    public EmployeeEntity findByEmail(String email);
}