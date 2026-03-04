package com.example.BookApplication.Service;

import com.example.BookApplication.DTO.LoginRequest;
import com.example.BookApplication.DTO.LoginResponse;
import com.example.BookApplication.DTO.SignupRequest;
import com.example.BookApplication.Entity.User;
import com.example.BookApplication.Repository.UserRepository;
import com.example.BookApplication.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public LoginResponse register(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return new LoginResponse(null, null, null, "Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return new LoginResponse(null, null, null, "Email already exists");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEnabled(true);

        User registeredUser = userRepository.save(user);
        String token = tokenProvider.generateToken(registeredUser.getUsername());

        return new LoginResponse(token, registeredUser.getUsername(), registeredUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = tokenProvider.generateToken(user.getUsername());

            return new LoginResponse(token, user.getUsername(), user.getEmail(), "Login successful");
        } catch (Exception e) {
            return new LoginResponse(null, null, null, "Login failed: " + e.getMessage());
        }
    }
}
