package com.Address.AddressBookApp.serviceTest;

import com.Address.AddressBookApp.dto.AuthUserDTO;
import com.Address.AddressBookApp.dto.ContactDTO;
import com.Address.AddressBookApp.dto.LoginDTO;
import com.Address.AddressBookApp.entity.ContactEntity;
import com.Address.AddressBookApp.interfaces.AuthInterface;
import com.Address.AddressBookApp.interfaces.ContactInterface;
import com.Address.AddressBookApp.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ContactServiceTest {

    @Autowired
    AuthInterface authInterface;

    @Autowired
    ContactInterface contactInterface;

    @Autowired
    ContactRepository contactRepository;

    MockHttpServletRequest request = new MockHttpServletRequest();

    Long id = -1L;

    @BeforeEach //setup
    public void registerAndLoginDummyUser(){
        // Clear the test db for a fresh start
        authInterface.clear();
        contactInterface.clear();

        // Creating a dummy user
        AuthUserDTO dummyUser = new AuthUserDTO("DummyFirstName", "DummyLastName", "DummyEmail@gmail.com", "DummyPass12#");

        // Registering a dummy user, so we can implement other test cases
        authInterface.register(dummyUser);

        // Logging in the dummy user
        LoginDTO userLogin = new LoginDTO("DummyEmail@gmail.com", "DummyPass12#");

        MockHttpServletResponse response = new MockHttpServletResponse();

        authInterface.login(userLogin, response);

        String auth = response.getHeader("Authorization");

        request.addHeader("Authorization", auth);
    }

    @AfterEach
    public void clearDb(){
        authInterface.clear();
        contactInterface.clear();
    }

    @Test
    public void createTest(){
        ContactDTO newContact = new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras");

        ContactDTO resDto = contactInterface.create(newContact, request);

        assertNotNull(resDto);

        // Finding the contact in the database
        ContactEntity foundContact = contactRepository.findByEmail("dhruv@gmail.com");

        assertNotNull(foundContact);
        assertEquals(843249724L, foundContact.getPhoneNumber());
    }

    @Test
    public void createDuplicateContactTest() {
        // Creating a contact
        ContactDTO newContact = new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras");
        contactInterface.create(newContact, request);

        // Trying to create the same contact again should throw an exception
        assertThrows(RuntimeException.class, () -> contactInterface.create(newContact, request),
                "Duplicate contact creation should throw an exception");
    }

    @Test
    public void getTest(){
        // Creating a contact
        ContactDTO newContact = new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras");
        ContactDTO resDto = contactInterface.create(newContact, request);
        assertNotNull(resDto);

        // Action
        ContactDTO getDto = contactInterface.get(resDto.getId(), request);

        // Assert
        assertEquals(getJSON(resDto), getJSON(getDto));
    }

    @Test
    public void getNonExistentContactTest() {
        // Trying to get a contact that doesn't exist should throw an exception
        assertThrows(RuntimeException.class, () -> contactInterface.get(99999L, request),
                "Fetching a non-existent contact should throw an exception");
    }

    @Test
    public void getAllTest(){
        // Creating a list of contacts to add in the database one at a time
        List<ContactDTO> l1 = new ArrayList<>();

        l1.add(new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras"));
        l1.add(new ContactDTO("Vikas Singh", "vikas@gmail.com", 783573595L, "Aligarh"));
        l1.add(new ContactDTO("Ashwani Sahu", "ashwani@gmail.com", 973847535L, "Etah"));

        // Adding the contacts to test db and setting the response dtos in the original list
        for (int i=0 ; i<l1.size() ; i++) {
            l1.set(i,contactInterface.create(l1.get(i), request));
        }

        List<ContactDTO> resList = contactInterface.getAll(request);

        // Check all the contacts are received or not
        for(int i = 0; i<l1.size(); i++){
            assertEquals(getJSON(l1.get(i)), getJSON(resList.get(i)));
        }
    }

    @Test
    public void editTest(){
        // Creating a contact
        ContactDTO newContact = new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras");

        ContactDTO resDto1 = contactInterface.create(newContact, request);

        assertNotNull(resDto1);

        // Edit the previous contact with a new one
        ContactDTO editContact = new ContactDTO("Aman Verma", "aman@gmail.com", 984534535L, "Aligarh", resDto1.getId());

        ContactDTO resDto2 = contactInterface.edit(editContact, resDto1.getId(), request);

        // Assert the changes made with intended changes
        assertEquals(getJSON(editContact), getJSON(resDto2));
    }

    @Test
    public void editNonExistentContactTest() {
        // Trying to edit a contact that doesn't exist should throw an exception
        ContactDTO editContact = new ContactDTO("Aman Verma", "aman@gmail.com", 984534535L, "Aligarh", 99999L);

        assertThrows(RuntimeException.class, () -> contactInterface.edit(editContact, 99999L, request),
                "Editing a non-existent contact should throw an exception");
    }

    @Test
    public void deleteTest(){
        // Creating a contact
        ContactDTO newContact = new ContactDTO("Dhruv Varshney", "dhruv@gmail.com", 843249724L, "Hathras");
        ContactDTO resDto1 = contactInterface.create(newContact, request);
        assertNotNull(resDto1);

        // Delete the contact created
        String res = contactInterface.delete(resDto1.getId(), request);

        // Match the res with expected response
        assertEquals("Contact deleted", res, "Response didn't match");

        // Double check in db if removed or not
        ContactEntity foundContact = contactRepository.findByEmail("dhruv@gmail.com");

        // foundContact should be null if removed from test db
        assertNull(foundContact);
    }

    @Test
    public void deleteNonExistentContactTest() {
        // Trying to delete a contact that doesn't exist should throw an exception
        assertThrows(RuntimeException.class, () -> contactInterface.delete(99999L, request),
                "Deleting a non-existent contact should throw an exception");
    }

    public String getJSON(Object object){
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(object);
        }
        catch(JsonProcessingException e){
            System.out.println("Exception : Conversion error from Java Object to JSON");
        }
        return null;
    }
}