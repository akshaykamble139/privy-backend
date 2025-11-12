package com.akshay.privy_backend.service;

import java.util.List;

import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.entity.User;

public interface DeviceService {

	void createDeviceAndPublicKeys(User user, String deviceName, String publicKey, Boolean isUserRegistering);
	void saveDeviceAndPublicKeys(User user, String deviceName, String publicKey);
	List<DeviceResponse> getAllPublicKeysByUsername(String username);
}
