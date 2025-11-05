package com.akshay.privy_backend.service;

import java.time.Instant;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akshay.privy_backend.dto.LoginRequest;
import com.akshay.privy_backend.dto.LoginResponse;
import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.UserRepository;
import com.akshay.privy_backend.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtService jwtService;
		
	@Override
	public UserResponse register(RegisterRequest request) {
		if (request == null) {
            throw new IllegalArgumentException("Request object is null");
		}
		
		if (request.getUsername() == null || request.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username is too short");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password is too short");
        }
        
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("Username is already taken");
        });
        
        String hash = passwordEncoder.encode(request.getPassword());
        
        User user = new User();
        
        user.setUsername(request.getUsername());
        user.setPasswordHash(hash);
        user.setCreatedAt(Instant.now());
        
        User savedUser = userRepository.save(user);
        
        UserResponse response = new UserResponse();
        
        response.setUsername(savedUser.getUsername());
        response.setId(savedUser.getId());
        
        return response;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		if (request == null) {
            throw new IllegalArgumentException("Request object is null");
		}
		
		if (request.getUsername() == null || request.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username is too short");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password is too short");
        }
        
        User user = userRepository.findByUsername(request.getUsername())
        		.orElseThrow(() -> new NoSuchElementException("No such username exists"));
                
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Incorrect password");
        }
        
        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generateToken(request.getUsername()));
        
        return response;
	}
}
