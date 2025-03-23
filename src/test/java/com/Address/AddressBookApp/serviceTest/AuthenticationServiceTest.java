package com.Address.AddressBookApp.serviceTest;

import com.Address.AddressBookApp.dto.AuthUserDTO;
import com.Address.AddressBookApp.dto.LoginDTO;
import com.Address.AddressBookApp.dto.PassDTO;
import com.Address.AddressBookApp.entity.AuthUser;
import com.Address.AddressBookApp.interfaces.AuthInterface;
import com.Address.AddressBookApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthenticationServiceTest {

    @Autowired
    AuthInterface authInterface;

    @Autowired
    UserRepository userRepository;

    @BeforeEach //setup
    public void registerDummyUser(){
        // Clear the test database for a fresh start
        authInterface.clear();

        // Creating a dummy user
        AuthUserDTO dummyUser = new AuthUserDTO("DummyFirstName", "DummyLastName", "DummyEmail@gmail.com", "DummyPass12#");

        // Registering a dummy user, so we can implement other test cases
        authInterface.register(dummyUser);
    }

    @AfterEach // Clear test database
    public void clearDb(){
        authInterface.clear();
    }

    @Test
    public void registerTest(){
        // Arrange --> Action --> Assert

        // Arrange
        AuthUserDTO tempUser = new AuthUserDTO("FirstName", "LastName", "TempEmail@gmail.com", "Temppass12#");

        // Action
        String res = authInterface.register(tempUser);

        // Assert
        assertEquals("User registered", res, "User registration test failed");
    }

    @Test
    public void registerDuplicateUserTest() {
        // Trying to register the same user again should throw an exception
        AuthUserDTO duplicateUser = new AuthUserDTO("DummyFirstName", "DummyLastName", "DummyEmail@gmail.com", "DummyPass12#");

        assertThrows(RuntimeException.class, () -> authInterface.register(duplicateUser),
                "Duplicate registration should throw an exception");
    }

    @Test
    public void loginTest(){
        // Arrange
        LoginDTO userLogin = new LoginDTO("DummyEmail@gmail.com", "DummyPass12#");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        String res = authInterface.login(userLogin, response);

        // Assert
        assertNotNull(res);

        // User should be logged in
        assertTrue(res.contains("User logged in"), "Log in test failure");

        // Checking the creation of token in cookies of response
        String jwtToken = response.getHeader("Authorization");

        System.out.println(jwtToken);

        assertNotNull(jwtToken, "JWT Token should be set in response");
    }

    @Test
    public void loginWithInvalidCredentialsTest() {
        // Trying to login with incorrect credentials should throw an exception
        LoginDTO invalidUser = new LoginDTO("DummyEmail@gmail.com", "WrongPass123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(RuntimeException.class, () -> authInterface.login(invalidUser, response),
                "Invalid login should throw an exception");
    }

    @Test
    public void forgotPasswordTest(){
        PassDTO newPass = new PassDTO("Hello1@$");
        AuthUserDTO resDto = authInterface.forgotPassword(newPass, "DummyEmail@gmail.com");
        assertNotNull(resDto);
        assertEquals("Hello1@$", resDto.getPassword(), "Password did not match after forgot");
    }

    @Test
    public void forgotPasswordForNonExistentUserTest() {
        // Trying to reset password for a non-existent user should throw an exception
        PassDTO newPass = new PassDTO("Hello1@$");

        assertThrows(RuntimeException.class, () -> authInterface.forgotPassword(newPass, "nonexistent@gmail.com"),
                "Forgot password for non-existent user should throw an exception");
    }

    @Test
    public void resetPasswordTest(){
        String email = "DummyEmail@gmail.com";
        String currentPass = "DummyPass12#";
        String newPass = "NewPass12#";

        String res = authInterface.resetPassword(email, currentPass, newPass);

        // Checking response
        assertEquals("Password reset successful!", res);

        AuthUser foundUser = userRepository.findByEmail("DummyEmail@gmail.com");

        // Checking the password update
        assertEquals("NewPass12#", foundUser.getPassword());
    }

    @Test
    public void resetPasswordWithWrongCurrentPasswordTest() {
        // Trying to reset password with the wrong current password should throw an exception
        assertThrows(RuntimeException.class, () -> authInterface.resetPassword("DummyEmail@gmail.com", "WrongPass123", "NewPass12#"),
                "Reset password with wrong current password should throw an exception");
    }
}