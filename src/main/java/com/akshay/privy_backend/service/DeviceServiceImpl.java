package com.akshay.privy_backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.entity.Device;
import com.akshay.privy_backend.entity.DevicePublicKey;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.DeviceRepository;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceRepository deviceRepository;

	@Override
	public void createDeviceAndPublicKeys(User user, String deviceName, String publicKey, Boolean isUserRegistering) {
		
		Device device = new Device();
        
        device.setDeviceName(deviceName);
        device.setCreatedAt(Instant.now());
        device.setLastSeenAt(Instant.now());
        device.setUser(user);
        
        DevicePublicKey devicePublicKey = new DevicePublicKey();
        
        devicePublicKey.setCreatedAt(Instant.now());
        devicePublicKey.setDevice(device);
        devicePublicKey.setPublicKey(publicKey);
        
        device.getPublicKeys().add(devicePublicKey);
                
        if (isUserRegistering) {
        	user.getDevices().add(device);
        }
        else {
        	deviceRepository.save(device);
        }
  	}

	@Override
	public void saveDeviceAndPublicKeys(User user, String deviceName, String publicKey) {
		
		Optional<Device> deviceOptional = deviceRepository
        		.findByUserUsernameAndDeviceName(user.getUsername(), deviceName);
        
        if (deviceOptional.isPresent()) {
        	Device device = deviceOptional.get();
        	device.setLastSeenAt(Instant.now());
        	deviceRepository.save(device);
        }
        else {
        	if (publicKey == null || publicKey.isBlank()) {
                throw new IllegalArgumentException("Device public key is too short");
            }
        	createDeviceAndPublicKeys(user, deviceName, publicKey, false);
        }
	}

	@Override
	public List<DeviceResponse> getAllPublicKeysByUsername(String username) {
		
		List<Device> devices = deviceRepository.findByUserUsername(username);
		
		ArrayList<DeviceResponse> result = new ArrayList<>();
		
		for (Device device: devices) {
			for (DevicePublicKey key: device.getPublicKeys()) {
				DeviceResponse response = new DeviceResponse();
				response.setDeviceName(device.getDeviceName());
				response.setPublicKey(key.getPublicKey());
				result.add(response);
			}
		}
		
		return result;
	}
}
