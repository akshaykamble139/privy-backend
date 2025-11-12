package com.akshay.privy_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akshay.privy_backend.dto.DeviceRegisterRequest;
import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.service.AuthService;
import com.akshay.privy_backend.service.DeviceService;

@RestController
@RequestMapping("/api")
public class DeviceController {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private AuthService authService;

	@GetMapping("/users/{username}/devices")
	public List<DeviceResponse> getAllPublicKeysByUsername(@PathVariable String username) {
		return deviceService.getAllPublicKeysByUsername(username);
	}
	
    @PostMapping("/devices/register")
    public ResponseEntity<?> registerNewDevice(@RequestBody DeviceRegisterRequest request) {
    	
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	
    	User user = authService.findByUsername(username);
    	
        deviceService.saveDeviceAndPublicKeys(user, request.getDeviceName(), request.getPublicKey());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Device registered successfully"));
    	
    }

}
