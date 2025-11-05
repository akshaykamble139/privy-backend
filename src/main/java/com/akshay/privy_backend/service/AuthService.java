package com.akshay.privy_backend.service;

import com.akshay.privy_backend.dto.LoginRequest;
import com.akshay.privy_backend.dto.LoginResponse;
import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;

public interface AuthService {
	UserResponse register(RegisterRequest request);	
	LoginResponse login(LoginRequest request);	
}
