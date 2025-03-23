package com.Address.AddressBookApp.service;

import com.Address.AddressBookApp.dto.AuthUserDTO;
import com.Address.AddressBookApp.dto.LoginDTO;
import com.Address.AddressBookApp.dto.PassDTO;
import com.Address.AddressBookApp.interfaces.AuthInterface;
import com.Address.AddressBookApp.entity.AuthUser;
import com.Address.AddressBookApp.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthenticationService implements AuthInterface {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    RedisTokenService redisTokenService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public String register(AuthUserDTO user) {
        try {
            List<AuthUser> l1 = userRepository.findAll().stream().filter(authuser -> user.getEmail().equals(authuser.getEmail())).collect(Collectors.toList());

            if (l1.size() > 0) {
                throw new RuntimeException();
            }

            //creating hashed password using bcrypt
            String hashPass = bCryptPasswordEncoder.encode(user.getPassword());

            //creating new user
            AuthUser newUser = new AuthUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), hashPass);

            //setting the new hashed password
            newUser.setHashPass(hashPass);

            //saving the user in the database
            userRepository.save(newUser);

            log.info("User saved in database : {}", getJSON(newUser));

            //sending the custom message to Message Producer
            String customMessage = "REGISTER|"+user.getEmail()+"|"+user.getFirstName();
            messageProducer.sendMessage(customMessage);

            return "User registered";
        }
        catch(RuntimeException e){
            log.error("User already registered with email: {} Exception : {}", user.getEmail(), e);
        }

        return null;
    }


    public String login(LoginDTO user, HttpServletResponse response){
        try {
            List<AuthUser> l1 = userRepository.findAll().stream().filter(authuser -> authuser.getEmail().equals(user.getEmail())).collect(Collectors.toList());
            if (l1.size() == 0) {
                throw new RuntimeException();
            }
            AuthUser foundUser = l1.get(0);

            // Matching the stored hashed password with the password provided by user
            if (!bCryptPasswordEncoder.matches(user.getPassword(), foundUser.getHashPass())) {
                log.error("Invalid password entered for email {} where entered password is {}", user.getEmail(), user.getPassword());
                return "Invalid password";
            }

            // Creating Jwt Token
            String token = jwtTokenService.createToken(foundUser.getId());

            //setting token in header of response
            System.out.println(token);
            response.addHeader("Authorization", "Bearer : "+token);

            // Setting token for user login
            foundUser.setToken(token);

            // Saving the current status of user in database
            userRepository.save(foundUser);

            log.info("User logged in with email {}", user.getEmail());

            return "User logged in" + "\ntoken: " + token;
        }
        catch(RuntimeException e){
            log.error("User already registered with email: {} Exception : {}", user.getEmail(), e);
        }
        return null;
    }

    public AuthUserDTO forgotPassword(PassDTO pass, String email){
        try {
            AuthUser foundUser = userRepository.findByEmail(email);

            if (foundUser == null) {
                throw new RuntimeException();
            }
            String hashpass = bCryptPasswordEncoder.encode(pass.getPassword());

            foundUser.setPassword(pass.getPassword());
            foundUser.setHashPass(hashpass);

            log.info("Hashpassword: {} for password: {} saved for user: {}", hashpass, pass.getPassword(),getJSON(foundUser));

            userRepository.save(foundUser);

            //sending the custom message to Message Producer
            String customMessage = "FORGOT|"+foundUser.getEmail()+"|"+foundUser.getFirstName();
            messageProducer.sendMessage(customMessage);

            AuthUserDTO resDto = new AuthUserDTO(foundUser.getFirstName(), foundUser.getLastName(), foundUser.getEmail(), foundUser.getPassword(), foundUser.getId());

            return resDto;
        }
        catch(RuntimeException e){
            log.error("User not registered with email: {} Exception : {}", email, e);
        }

        return null;
    }

    public String resetPassword(String email, String currentPass, String newPass) {

        AuthUser foundUser = userRepository.findByEmail(email);
        if (foundUser == null) {
            return "User not registered!";
        }

        if (!bCryptPasswordEncoder.matches(currentPass, foundUser.getHashPass())) {
            return "Incorrect password!";
        }

        String hashpass = bCryptPasswordEncoder.encode(newPass);

        foundUser.setHashPass(hashpass);
        foundUser.setPassword(newPass);

        userRepository.save(foundUser);
        log.info("Hashpassword: {} for password: {} saved for user: {}", hashpass, newPass, getJSON(foundUser));

        String customMessage = "RESET|"+foundUser.getEmail()+"|"+foundUser.getFirstName();
        messageProducer.sendMessage(customMessage);

        return "Password reset successful!";
    }

    public String clear(){
        userRepository.deleteAll();
        log.info("All data inside db is deleted");
        return "Database cleared";
    }

    public String getJSON(Object object){
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(object);
        }
        catch(JsonProcessingException e){
            log.error("Reason : {} Exception : {}", "Conversion error from Java Object to JSON");
        }
        return null;
    }
}