package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.akshay.privy_backend.dto.LoginRequest;
import com.akshay.privy_backend.dto.LoginResponse;
import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.UserRepository;
import com.akshay.privy_backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtService jwtService;
	
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
	
	@Test
	public void loginNullTest() {
		
		LoginRequest request = null;
		
		boolean actualResult = true;
		
		try {
			authService.login(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void loginShortUsernameTest() {
		
		LoginRequest request = new LoginRequest();
		
		boolean actualResult = true;
		
		try {
			authService.login(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
		
		actualResult = true;
		request.setUsername("Bo");
		
		try {
			authService.login(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void loginShortPasswordTest() {
		
		LoginRequest request = new LoginRequest();
		request.setUsername("akshay");
		
		boolean actualResult = true;
		
		try {
			authService.login(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
		
		actualResult = true;
		request.setPassword("aks@123");
		
		try {
			authService.login(request);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void loginNoUsernameFoundTest() {
		
		LoginRequest request = new LoginRequest();
		request.setUsername("akshay");
		request.setPassword("akshay123");
		
		User user = new User();
		user.setUsername("akshay");
		
		Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		
		boolean actualResult = true;
		
		try {
			authService.login(request);
		} catch (NoSuchElementException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void loginIncorrectPasswordTest() {
		
		LoginRequest request = new LoginRequest();
		request.setUsername("akshay");
		request.setPassword("akshay123");
		
		User user = new User();
		user.setUsername("akshay");
		user.setPasswordHash("xttfrdgfty8yftytgyftftgyf");
		
		Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
		
		Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
		
		boolean actualResult = true;
		
		try {
			authService.login(request);
		} catch (BadCredentialsException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);	
	}
	
	@Test
	public void loginCorrectUserTest() {
		
		LoginRequest request = new LoginRequest();
		request.setUsername("akshay");
		request.setPassword("akshay123");
				
		User user = new User();
		user.setUsername("akshay");
		user.setPasswordHash("xttfrdgfty8yftytgyftftgyf");
		user.setId(UUID.randomUUID());
		user.setCreatedAt(Instant.now());
		
		Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
		
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJha3NoYXkiLCJpYXQiOjE3NjIyOTc1NTEsImV4cCI6MTc2MjM4Mzk1MX0"
				+ ".GAotf3u-WK9QhAk-K7rGOjy9yTP7LSou1ZII-vFiI98";
		
		Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

		Mockito.when(jwtService.generateToken(anyString())).thenReturn(token);

		LoginResponse actualResult = authService.login(request);
						
		Assertions.assertEquals(token, actualResult.getToken());	
	}

}
