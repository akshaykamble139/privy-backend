package com.akshay.privy_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;
import com.akshay.privy_backend.service.AuthService;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
		UserResponse response;
		try {
			response = authService.register(request);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
				
		return ResponseEntity.ok(response);
	}
}
