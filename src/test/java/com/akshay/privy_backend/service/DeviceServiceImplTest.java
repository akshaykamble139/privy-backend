package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.entity.Device;
import com.akshay.privy_backend.entity.DevicePublicKey;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.DeviceRepository;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTest {

	@Mock
	private DeviceRepository deviceRepository;
	
	@InjectMocks
	private DeviceServiceImpl deviceService;

	@Test
	public void createDeviceAndPublicKeysTest() {
		User user = new User();
		deviceService.createDeviceAndPublicKeys(user, "Chrome", "publicKey1", true);
		deviceService.createDeviceAndPublicKeys(user, "Chrome", "publicKey2", false);
	}
	
	@Test
	public void saveDeviceAndPublicKeysTest() {
		User user = new User();
		user.setUsername("akshay");
		
		Device device = new Device();
		
		Mockito.when(deviceRepository.findByUserUsernameAndDeviceName(
				anyString(), anyString())).thenReturn(Optional.of(device));
		
		deviceService.saveDeviceAndPublicKeys(user, "Chrome", "publicKey");
		
		Mockito.when(deviceRepository.findByUserUsernameAndDeviceName(
				anyString(), anyString())).thenReturn(Optional.empty());
		
		boolean actualResult = true;
		try {
			deviceService.saveDeviceAndPublicKeys(user, "Chrome", null);
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);
		
		actualResult = true;
		try {
			deviceService.saveDeviceAndPublicKeys(user, "Chrome", "");
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(false, actualResult);
		
		actualResult = true;
		try {
			deviceService.saveDeviceAndPublicKeys(user, "Chrome", "publicKey");
		} catch (IllegalArgumentException e) {
			actualResult = false;
		}
		
		Assertions.assertEquals(true, actualResult);
	}
	
	@Test
	public void getAllPublicKeysByUsernameTest() {
		
		ArrayList<Device> devices = new ArrayList<Device>();
		
		Device device1 = new Device();
		device1.setDeviceName("Chrome");
		
		DevicePublicKey key1 = new DevicePublicKey();
		key1.setPublicKey("publicKey1");
		key1.setDevice(device1);
		
		device1.setPublicKeys(List.of(key1));
		
		Device device2 = new Device();
		device2.setDeviceName("Pixel");

		DevicePublicKey key2 = new DevicePublicKey();
		key2.setPublicKey("publicKey2");
		key2.setDevice(device2);

		device2.setPublicKeys(List.of(key2));
		
		devices.add(device1);
		devices.add(device2);
		
		Mockito.when(deviceRepository.findByUserUsername(anyString())).thenReturn(devices);

		List<DeviceResponse> responses = deviceService.getAllPublicKeysByUsername("akshay");
		
		Assertions.assertEquals(2, responses.size());
	}
 
}
