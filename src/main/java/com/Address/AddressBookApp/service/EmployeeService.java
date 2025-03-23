package com.Address.AddressBookApp.service;

import com.Address.AddressBookApp.dto.EmployeeDTO;
import com.Address.AddressBookApp.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService implements EmployeeInterface {
    @Autowired
    EmployeeRepository empRepository;

    public ResponseDTO res(String message, String status){
        return new ResponseDTO(message, status);
    }

    public EmployeeDTO get(Long id){
        EmployeeEntity foundEmp = empRepository.findById(id).orElseThrow(()->new RuntimeException("Can't find employee with this id"));
        EmployeeDTO edto = new EmployeeDTO(foundEmp.getName(), foundEmp.getEmail(), foundEmp.getId());
        return edto;
    }

    public EmployeeDTO create(EmployeeDTO user){
        EmployeeEntity newUser = new EmployeeEntity(user.getName(), user.getEmail());
        empRepository.save(newUser);
        EmployeeDTO edto = new EmployeeDTO(newUser.getName(), newUser.getEmail(), newUser.getId());
        System.out.println(newUser.getId());
        return edto;
    }

    public List<EmployeeDTO> getAll(){
        return empRepository.findAll().stream().map(entity -> {
            EmployeeDTO edto = new EmployeeDTO(entity.getName(), entity.getEmail(), entity.getId());
            return edto;
        }).collect(Collectors.toList());
    }

    public EmployeeDTO edit(EmployeeDTO user, Long id){
        EmployeeEntity foundEmp = empRepository.findById(id).orElseThrow(()->new RuntimeException("Can't find employee with this id"));
        foundEmp.setName(user.getName());
        foundEmp.setEmail(user.getEmail());
        empRepository.save(foundEmp);
        EmployeeDTO edto = new EmployeeDTO(foundEmp.getName(), foundEmp.getEmail(), foundEmp.getId());
        return edto;
    }

    public String delete(Long id){
        EmployeeEntity foundUser = empRepository.findById(id).orElseThrow(()->new RuntimeException("Can't find user with this id"));
        empRepository.delete(foundUser);
        return "Deleted successfully";
    }

    public String clear(){
        empRepository.deleteAll();
        return "Database cleared";
    }
}