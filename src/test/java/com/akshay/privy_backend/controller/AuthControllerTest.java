package com.akshay.privy_backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.akshay.privy_backend.config.SecurityConfig;
import com.akshay.privy_backend.dto.RegisterRequest;
import com.akshay.privy_backend.dto.UserResponse;
import com.akshay.privy_backend.service.AuthService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private AuthService authService;
	
	
	@Test
	public void registerValidUserTest() {
		
        UserResponse response = new UserResponse();
        response.setUsername("akshay");
        response.setId(UUID.randomUUID());
        
        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(response);
        
        try {
			mockMvc.perform(post("/api/auth/register")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{ \"username\": \"akshay\", \"password\": \"akshay123\" }"))
				   .andExpect(status().isOk())
				   .andExpect(jsonPath("$.username").value("akshay"))
				   .andExpect(jsonPath("$.id").value(response.getId().toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void registerInvalidUsernameTest() {
		
        UserResponse response = new UserResponse();
        response.setUsername("akshay");
        response.setId(UUID.randomUUID());
        
        Mockito.when(authService.register(any(RegisterRequest.class))).thenThrow(new IllegalArgumentException("Username is too short"));
        
        try {
			mockMvc.perform(post("/api/auth/register")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{ \"username\": \"ak\", \"password\": \"akshay123\" }"))
				   .andExpect(status().isBadRequest());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void registerInvalidPasswordTest() {
		
        UserResponse response = new UserResponse();
        response.setUsername("akshay");
        response.setId(UUID.randomUUID());
        
        Mockito.when(authService.register(any(RegisterRequest.class))).thenThrow(new IllegalArgumentException("Password is too short"));
        
        try {
			mockMvc.perform(post("/api/auth/register")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{ \"username\": \"tom\", \"password\": \"tom123\" }"))
				   .andExpect(status().isBadRequest());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void registerUsernameAlreadyTakenTest() {
		
        UserResponse response = new UserResponse();
        response.setUsername("akshay");
        response.setId(UUID.randomUUID());
        
        Mockito.when(authService.register(any(RegisterRequest.class))).thenThrow(new IllegalStateException("Username is already taken"));
        
        try {
			mockMvc.perform(post("/api/auth/register")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{ \"username\": \"tom\", \"password\": \"tom123\" }"))
				   .andExpect(status().isConflict());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
