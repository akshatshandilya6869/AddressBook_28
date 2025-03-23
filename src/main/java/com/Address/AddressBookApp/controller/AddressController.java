package com.example.AddressBookApp.controller;

import com.Address.AddressBookApp.dto.EmployeeDTO;
import com.Address.AddressBookApp.dto.ResponseDTO;
import com.Address.AddressBookApp.interfaces.EmployeeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    EmployeeInterface Iemployee;

    // UC1 (Handling REST API using ResponseDTO without service layer)

    @GetMapping("/res/get/{id}")
    public ResponseDTO get1(@PathVariable Long id){
        return new ResponseDTO("API triggered", "Success");
    }

    @PostMapping("/res/create")
    public ResponseDTO create1(@RequestBody EmployeeDTO user) {
        return new ResponseDTO("API triggered", "Success");
    }

    @GetMapping("/res/getAll")
    public ResponseDTO getAll1(){
        return new ResponseDTO("API triggered", "Success");
    }

    @PutMapping("/res/edit/{id}")
    public ResponseDTO edit1(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return new ResponseDTO("API triggered", "Success");
    }

    @DeleteMapping("/res/delete/{id}")
    public ResponseDTO delete1(@PathVariable Long id){
        return new ResponseDTO("API triggered", "Success");
    }






    //UC2 (Handling REST API using service layer without database)

    @GetMapping("/res2/get/{id}")
    public ResponseDTO get2(@PathVariable Long id){
        return Iemployee.res("API triggered", "Success");
    }

    @PostMapping("/res2/create")
    public ResponseDTO create2(@RequestBody EmployeeDTO user){
        return Iemployee.res("API triggered", "Success");
    }

    @GetMapping("/res2/getAll")
    public ResponseDTO getAll2(){
        return Iemployee.res("API triggered", "Success");
    }

    @PutMapping("/res2/edit/{id}")
    public ResponseDTO edit2(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return Iemployee.res("API triggered", "Success");
    }

    @DeleteMapping("/res2/delete/{id}")
    public ResponseDTO delete2(@PathVariable Long id){
        return Iemployee.res("API triggered", "Success");
    }
}