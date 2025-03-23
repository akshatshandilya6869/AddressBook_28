package com.Address.AddressBookApp.interfaces;

import com.Address.AddressBookApp.dto.AuthUserDTO;
import com.Address.AddressBookApp.dto.LoginDTO;
import com.Address.AddressBookApp.dto.PassDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthInterface {
    public String register (AuthUserDTO user) throws Exception;
    public String login(LoginDTO user);
    public AuthUserDTO forgotPassword(PassDTO pass, String email) throws Exception;
    public String resetPassword(String email, String currentPass, String newPass) throws Exception;
}