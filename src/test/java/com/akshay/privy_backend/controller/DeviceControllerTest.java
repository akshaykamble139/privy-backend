package com.akshay.privy_backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.akshay.privy_backend.config.SecurityConfig;
import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.security.JwtAuthenticationFilter;
import com.akshay.privy_backend.service.AuthService;
import com.akshay.privy_backend.service.DeviceService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(DeviceController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class DeviceControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private DeviceService deviceService;
	
	@MockitoBean
	private AuthService authService;
	
	@MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Test
	public void getAllPublicKeysByUsernameTest() {
		ArrayList<DeviceResponse> list = new ArrayList<>();
		
		
		DeviceResponse response1 = new DeviceResponse();
		response1.setDeviceName("Chrome");
		response1.setPublicKey("publicKey1");
		
		DeviceResponse response2 = new DeviceResponse();
		response2.setDeviceName("Pixel");
		response2.setPublicKey("publicKey2");
		
		list.add(response1);
		list.add(response2);

        Mockito.when(deviceService.getAllPublicKeysByUsername(anyString())).thenReturn(list);
        
        try {
        	mockMvc.perform(get("/api/users/akshay/devices")
        		   .contentType(MediaType.APPLICATION_JSON))
        		   .andExpect(status().isOk())
        		   .andExpect(jsonPath("$[0].deviceName").value("Chrome"))
                   .andExpect(jsonPath("$[0].publicKey").value("publicKey1"))
                   .andExpect(jsonPath("$[1].deviceName").value("Pixel"))
                   .andExpect(jsonPath("$[1].publicKey").value("publicKey2"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void registerNewDeviceTest() {
		
		Authentication authentication = Mockito.mock(Authentication.class);
	    Mockito.when(authentication.getName()).thenReturn("akshay");
	    
	    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
	    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
	    
	    SecurityContextHolder.setContext(securityContext);
		
		User user = new User();
		user.setUsername("akshay");
		
        Mockito.when(authService.findByUsername(anyString())).thenReturn(user);
        
        Mockito.doNothing().when(deviceService).saveDeviceAndPublicKeys(any(User.class), anyString(),anyString());

        try {
			
        	mockMvc.perform(post("/api/devices/register")
        		   .contentType(MediaType.APPLICATION_JSON)
				   .content("{ \"deviceName\": \"Chrome\", \"publicKey\": \"publicKey\" }"))
        	       .andExpect(status().isCreated())
        	       .andExpect(jsonPath("$.message").value("Device registered successfully"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
