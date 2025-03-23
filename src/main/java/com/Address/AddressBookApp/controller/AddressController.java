package com.Address.AddressBookApp.controller;

import com.Address.AddressBookApp.dto.ContactDTO;
import com.Address.AddressBookApp.dto.ResponseDTO;
import com.Address.AddressBookApp.interfaces.ContactInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/address")
@Slf4j
public class AddressController {

    ObjectMapper obj = new ObjectMapper();

    @Autowired
    ContactInterface contactInterface;

    // UC1 (Handling REST API using ResponseDTO without service layer)

    @GetMapping("/res/get/{id}")
    public ResponseDTO get1(@Valid @PathVariable Long id){
        log.info("Get id: {}", id);
        return new ResponseDTO("API triggered", "Success");
    }

    @PostMapping("/res/create")
    public ResponseDTO create1(@Valid @RequestBody ContactDTO user){
        log.info("Create employee: {}", getJSON(user));
        return new ResponseDTO("API triggered", "Success");
    }

    @GetMapping("/res/getAll")
    public ResponseDTO getAll1(){
        log.info("Get all employees");
        return new ResponseDTO("API triggered", "Success");
    }

    @PutMapping("/res/edit/{id}")
    public ResponseDTO edit1(@Valid @RequestBody ContactDTO user, @Valid @PathVariable Long id){
        log.info("Edit id: {} and body: {}", id, getJSON(user));
        return new ResponseDTO("API triggered", "Success");
    }

    @DeleteMapping("/res/delete/{id}")
    public ResponseDTO delete1(@Valid @PathVariable Long id){
        log.info("Delete id: {}", id);
        return new ResponseDTO("API triggered", "Success");
    }






    //UC2 (Handling REST API using service layer without database)

    @GetMapping("/res2/get/{id}")
    public ResponseDTO get2(@Valid @PathVariable Long id){
        log.info("Get id: {}", id);
        return contactInterface.response("API triggered", "Success");
    }

    @PostMapping("/res2/create")
    public ResponseDTO create2(@Valid @RequestBody ContactDTO user){
        log.info("Create employee: {}", getJSON(user));
        return contactInterface.response("API triggered", "Success");
    }

    @GetMapping("/res2/getAll")
    public ResponseDTO getAll2(){
        log.info("Get all employee");
        return contactInterface.response("API triggered", "Success");
    }

    @PutMapping("/res2/edit/{id}")
    public ResponseDTO edit2(@Valid @RequestBody ContactDTO user,@Valid @PathVariable Long id){
        log.info("Edit id: {} and body: {}", id, getJSON(user));
        return contactInterface.response("API triggered", "Success");
    }

    @DeleteMapping("/res2/delete/{id}")
    public ResponseDTO delete2(@Valid @PathVariable Long id){
        log.info("Delete id: {}", id);
        return contactInterface.response("API triggered", "Success");
    }






    //UC3 (Handling REST API using service layer with database)

    @GetMapping("/get/{id}")
    public ContactDTO get3(@Valid @PathVariable Long id) {
        log.info("Get id: {}", id);
        return contactInterface.get(id);
    }

    @PostMapping("/create")
    public ContactDTO create3(@Valid @RequestBody ContactDTO user){
        log.info("Create employee: {}", getJSON(user));
        return contactInterface.create(user);
    }

    @GetMapping("/getAll")
    public List<ContactDTO> getAll3(){
        log.info("Get all employees");
        return contactInterface.getAll();
    }

    @PutMapping("/edit/{id}")
    public ContactDTO edit3(@Valid @RequestBody ContactDTO user, @Valid @PathVariable Long id){
        log.info("Edit id: {} and body: {}", id, getJSON(user));
        return contactInterface.edit(user, id);
    }

    @DeleteMapping("/delete/{id}")
    public String delete3(@Valid @PathVariable Long id){
        log.info("Delete id: {}", id);
        return contactInterface.delete(id);
    }

    @GetMapping("/clear")
    public String clear(){
        log.info("Database cleared");
        return contactInterface.clear();
    }

    public String getJSON(Object object){
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(object);
        }
        catch(JsonProcessingException e){
            log.error("Reason: {} Exception: {}", "Conversion error from Java Object to JSON");
        }
        return null;
    }
}