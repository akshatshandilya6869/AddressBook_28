package com.Address.AddressBookApp.interceptors;

import com.Address.AddressBookApp.entity.AuthUser;
import com.Address.AddressBookApp.repository.UserRepository;
import com.Address.AddressBookApp.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AddressInterceptor implements HandlerInterceptor {

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try{
            // Get token from cookies
            String token = null;

            String auth = request.getHeader("Authorization");

            if(auth == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            token = auth.substring(9);

            // If no token, reject request
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            System.out.println(token);

            // Decode token to get user ID
            Long userId = jwtTokenService.decodeToken(token);

            // Check for user in database with given id

            AuthUser foundUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            // Token is valid → Allow request
            return true;
        }
        catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}