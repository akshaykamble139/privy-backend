package com.akshay.privy_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ChatMemberDevicesResponse {
    private String username;
    private List<DeviceResponse> devices = new ArrayList<>();
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<DeviceResponse> getDevices() {
		return devices;
	}
	public void setDevices(List<DeviceResponse> devices) {
		this.devices = devices;
	}
}

