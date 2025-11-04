package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private AuthServiceImpl authService;
	
	@Test
	public void registerNullTest() {
		
		RegisterRequest request = null;
		
		boolean actualResult = true;
		
		try {
			authService.register(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void registerShortUsernameTest() {
		
		RegisterRequest request = new RegisterRequest();
		
		boolean actualResult = true;
		
		try {
			authService.register(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
		
		actualResult = true;
		request.setUsername("Bo");
		
		try {
			authService.register(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void registerShortPasswordTest() {
		
		RegisterRequest request = new RegisterRequest();
		request.setUsername("akshay");
		
		boolean actualResult = true;
		
		try {
			authService.register(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
		
		actualResult = true;
		request.setPassword("aks@123");
		
		try {
			authService.register(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void registerSameUsernameTest() {
		
		RegisterRequest request = new RegisterRequest();
		request.setUsername("akshay");
		request.setPassword("akshay123");
		
		User user = new User();
		user.setUsername("akshay");
		
		Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
		
		boolean actualResult = true;
		
		try {
			authService.register(request);
		} catch (IllegalStateException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void registerCorrectUserTest() {
		
		RegisterRequest request = new RegisterRequest();
		request.setUsername("akshay");
		request.setPassword("akshay123");
		
		Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		User user = new User();
		user.setUsername("akshay");
		user.setId(UUID.randomUUID());
		user.setCreatedAt(Instant.now());
		
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
		
		UserResponse actualResult;
		
		actualResult = authService.register(request);
				
		Assertions.assertEquals(request.getUsername(), actualResult.getUsername());	
	}
}
