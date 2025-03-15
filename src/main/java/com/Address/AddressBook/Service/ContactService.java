package com.Address.AddressBook.Service;

import com.Address.AddressBook.DTO.ContactDTO;
import com.Address.AddressBook.Model.Contact;
import com.Address.AddressBook.Repository.ContactRepository;
import com.Address.AddressBook.Mapper.ContactMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.Address.AddressBook.Exceptions.AddressBookException;
@Service
public class ContactService {

    ContactRepository contactRepository;
    ContactMapper contactMapper;

    public ContactService(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    public List<ContactDTO> getAllContacts() {
        try {
            return contactRepository.findAll()
                    .stream()
                    .map(contactMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AddressBookException("An error occurred while fetching contacts: " + e.getMessage());
        }
    }

    public ContactDTO getContactById(Long id) {
        try {
            Optional<Contact> contact = contactRepository.findById(id);
            if (contact.isEmpty()) {
                throw new AddressBookException("Contact with ID: " + id + " not found");
            }
            return contactMapper.toDTO(contact.get());
        } catch (Exception e) {
            throw new AddressBookException("Error retrieving contact with ID " + id + ": " + e.getMessage());
        }
    }

    public ContactDTO addContact(ContactDTO contactDTO) {
        try {
            Contact contact = contactMapper.toEntity(contactDTO);
            return contactMapper.toDTO(contactRepository.save(contact));
        } catch (Exception e) {
            throw new AddressBookException("Error adding new contact: " + e.getMessage());
        }
    }

    public ContactDTO updateContact(Long id, ContactDTO contactDTO) {
        try {
            Optional<Contact> existingContact = contactRepository.findById(id);
            if (existingContact.isEmpty()) {
                throw new AddressBookException("Contact with ID: " + id + " not found for update");
            }
            existingContact.get().setName(contactDTO.getName());
            existingContact.get().setEmail(contactDTO.getEmail());
            existingContact.get().setPhone(contactDTO.getPhone());

            return contactMapper.toDTO(contactRepository.save(existingContact.get()));
        } catch (Exception e) {
            throw new AddressBookException("Error updating contact with ID " + id + ": " + e.getMessage());
        }
    }

    public boolean deleteContact(Long id) {
        try {
            if (contactRepository.existsById(id)) {
                contactRepository.deleteById(id);
                return true;
            } else {
                throw new AddressBookException("Contact with ID: " + id + " not found for deletion");
            }
        } catch (Exception e) {
            throw new AddressBookException("Error deleting contact with ID " + id + ": " + e.getMessage());
        }
    }
}