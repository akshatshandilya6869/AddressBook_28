package com.Address.AddressBook.Controller;

import com.Address.AddressBook.DTO.ContactDTO;
import com.Address.AddressBook.Service.ContactService;
import lombok.extern.slf4j.Slf4j; // Lombok import for SLF4J logging
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.Address.AddressBook.Exceptions.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;


//================SLF4J added========//
@Slf4j  // This annotation enables logging
@RestController
@Validated  // Enable validation for the controller
@RequestMapping("/api/contacts")
public class ContactController {
    ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        try {
            log.info("Fetching all contacts");
            List<ContactDTO> contacts = contactService.getAllContacts();
            log.info("Retrieved {} contacts", contacts.size());
            return ResponseEntity.ok(contacts);
        } catch (AddressBookException e) {
            log.error("Error fetching contacts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // return empty contact list on error
        } catch (Exception e) {
            log.error("Unexpected error fetching contacts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // return empty list with internal server error
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        try {
            log.info("Fetching contact with ID: {}", id);
            ContactDTO contactDTO = contactService.getContactById(id);
            return ResponseEntity.ok(contactDTO);
        } catch (AddressBookException e) {
            log.error("Error fetching contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // return 404 if contact not found
        } catch (Exception e) {
            log.error("Unexpected error fetching contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // return 500 for any other unexpected error
        }
    }

    @PostMapping
    public ResponseEntity<ContactDTO> addContact(@RequestBody @Valid ContactDTO contactDTO) {
        try {
            log.info("Adding new contact: {}", contactDTO);
            ContactDTO savedContact = contactService.addContact(contactDTO);
            log.info("Added new contact with ID: {}", savedContact.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
        } catch (AddressBookException e) {
            log.error("Error adding contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // return 500 if error adding contact
        } catch (Exception e) {
            log.error("Unexpected error adding contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // return 500 for any other unexpected error
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateContact(@PathVariable Long id, @RequestBody @Valid ContactDTO contactDTO) {
        try {
            log.info("Updating contact with ID: {}", id);
            ContactDTO updatedContact = contactService.updateContact(id, contactDTO);
            return ResponseEntity.ok(updatedContact);
        } catch (AddressBookException e) {
            log.error("Error updating contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // return 404 if contact not found for update
        } catch (Exception e) {
            log.error("Unexpected error updating contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // return 500 for any other unexpected error
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        try {
            log.info("Deleting contact with ID: {}", id);
            boolean isDeleted = contactService.deleteContact(id);
            if (isDeleted) {
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
            }
        } catch (AddressBookException e) {
            log.error("Error deleting contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build(); // return 404 Not Found if contact not found for deletion
        } catch (Exception e) {
            log.error("Unexpected error deleting contact with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build(); // return 500 for any other unexpected error
        }
    }
}