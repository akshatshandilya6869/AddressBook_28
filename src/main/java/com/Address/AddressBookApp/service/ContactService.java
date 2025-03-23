package com.Address.AddressBookApp.service;

import com.Address.AddressBookApp.dto.ContactDTO;
import com.Address.AddressBookApp.dto.ResponseDTO;
import com.Address.AddressBookApp.entity.ContactEntity;
import com.Address.AddressBookApp.interfaces.ContactInterface;
import com.Address.AddressBookApp.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.info.Contact;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService implements ContactInterface {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    RedisTemplate<String, ContactDTO> cacheContacts;

    @Autowired
    RedisTemplate<String, List<ContactDTO>> cacheContactList;


    public ResponseDTO response(String message, String status){
        return new ResponseDTO(message, status);
    }

    public ContactDTO get(Long id, HttpServletRequest request){
        try {
            Long userId = getUserId(request);

            if(cacheContacts.opsForValue().get("Contact"+userId+":"+id) != null) {
                System.out.println("Done through caching");
                return cacheContacts.opsForValue().get("Contact" + userId + ":" + id);
            }

            System.out.println(userId);
            List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).collect(Collectors.toList());

            if(contacts.isEmpty()) {
                throw new RuntimeException();
            }

            ContactEntity foundContact = contacts.get(0);
            ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(), foundContact.getPhoneNumber(), foundContact.getAddress(), foundContact.getId());
            log.info("Contact DTO send for id: {} is : {}", id, getJSON(resDto));

            //save contact dto in the redis cache
            cacheContacts.opsForValue().set("Contact" + userId + ":" + id, resDto);

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Cannot find contact with id {}", id);
        }
        return null;
    }

    public ContactDTO create(ContactDTO user, HttpServletRequest request){
        try {
            ContactEntity foundEntity = contactRepository.findByEmail(user.getEmail());

            //fetching userId from token in cookies of user
            Long userId = getUserId(request);

            ContactEntity newUser = new ContactEntity(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), userId);
            contactRepository.save(newUser);
            log.info("Contact saved in db: {}", getJSON(newUser));
            ContactDTO resDto = new ContactDTO(newUser.getName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getAddress(), newUser.getId());
            log.info("Contact DTO sent: {}", getJSON(resDto));

            //add the new contact in the cached contact list
            List<ContactDTO> l1 = cacheContactList.opsForValue().get("Contact"+userId);

            if(l1 == null) {
                l1 = new ArrayList<>();
            }

            l1.add(resDto);
            cacheContactList.opsForValue().set("Contact"+userId, l1);

            return resDto;
        }
        catch(RuntimeException e){
            log.error("Exception : {} Reason : {}", e, "User already created with given email");
        }
        return null;
    }

    public List<ContactDTO> getAll(HttpServletRequest request){
        //fetching userId from token in cookies of user
        Long userId = getUserId(request);

        if(cacheContactList.opsForValue().get("Contact"+userId) != null) {
            System.out.println("Done through caching");
            return cacheContactList.opsForValue().get("Contact" + userId);
        }

        List<ContactDTO> result = contactRepository.findByUserId(userId).stream().map(entity -> {
            ContactDTO newUser = new ContactDTO(entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getAddress(), entity.getId());
            return newUser;
        }).collect(Collectors.toList());

        //save list in cache memory of redis
        cacheContactList.opsForValue().set("Contact"+userId, result);

        return result;
    }

    public ContactDTO edit(ContactDTO user, Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).collect(Collectors.toList());

        if(contacts.isEmpty()) {
            throw new RuntimeException("No contact with given id found");
        }

        ContactEntity foundContact = contacts.get(0);

        foundContact.setName(user.getName());
        foundContact.setEmail(user.getEmail());
        foundContact.setAddress(user.getAddress());
        foundContact.setPhoneNumber(user.getPhoneNumber());

        contactRepository.save(foundContact);
        log.info("Contact saved after editing in db is : {}", getJSON(foundContact));
        ContactDTO resDto = new ContactDTO(foundContact.getName(), foundContact.getEmail(),foundContact.getPhoneNumber(), foundContact.getAddress(), foundContact.getId());

        //reset the contacts list and the contact cache(to freshly search the updated contact next time from db)
        cacheContacts.delete("Contact"+userId+":"+id);
        cacheContactList.delete("Contact"+userId);

        return resDto;
    }

    public String delete(Long id, HttpServletRequest request){
        Long userId = getUserId(request);
        List<ContactEntity> contacts = contactRepository.findByUserId(userId).stream().filter(entity -> entity.getId().equals(id)).collect(Collectors.toList());

        if(contacts.size() == 0) {
            throw new RuntimeException("No contact with given id found");
        }

        ContactEntity foundUser = contacts.get(0);

        contactRepository.delete(foundUser);

        //reset the contacts list and the contact cache(to freshly search the updated contacts next time from db)
        cacheContacts.delete("Contact"+userId+":"+id);
        cacheContactList.delete("Contact"+userId);

        return "Contact deleted";
    }

    @Override
    public ContactDTO get(Long id) {
        return null;
    }

    @Override
    public ContactDTO create(ContactDTO user) {
        return null;
    }

    public String clear(){
        contactRepository.deleteAll();
        return "Database cleared";
    }

    @Override
    public List<ContactDTO> getAll() {
        return List.of();
    }

    @Override
    public ContactDTO edit(ContactDTO user, Long id) {
        return null;
    }

    @Override
    public String delete(Long id) {
        return "";
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

    public Long getUserId(HttpServletRequest request){
        //fetching token of logged in user
        String auth = request.getHeader("Authorization");
        System.out.println(auth.substring(9));
        if(auth == null) {
            throw new RuntimeException("Cannot find the login cookie");
        }
        //decode the user id from token in cookie using jwttokenservice
        Long userId = jwtTokenService.decodeToken(auth.substring(9));
        return userId;
    }
}