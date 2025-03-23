package com.Address.AddressBookApp.controller;

import com.Address.AddressBookApp.dto.EmployeeDTO;
import com.Address.AddressBookApp.dto.ResponseDTO;
import com.Address.AddressBookApp.interfaces.EmployeeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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






    //UC3 (Handling REST API using service layer with database)

    @GetMapping("/get/{id}")
    public EmployeeDTO get3(@PathVariable Long id){
        return Iemployee.get(id);
    }

    @PostMapping("/create")
    public EmployeeDTO create3(@RequestBody EmployeeDTO user){
        return Iemployee.create(user);
    }

    @GetMapping("/getAll")
    public List<EmployeeDTO> getAll3(){
        return Iemployee.getAll();
    }

    @PutMapping("/edit/{id}")
    public EmployeeDTO edit3(@RequestBody EmployeeDTO user, @PathVariable Long id){
        return Iemployee.edit(user, id);
    }

    @DeleteMapping("/delete/{id}")
    public String delete3(@PathVariable Long id){
        return Iemployee.delete(id);
    }

    @GetMapping("/clear")
    public String clear(){
        return Iemployee.clear();
    }
}