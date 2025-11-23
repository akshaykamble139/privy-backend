package com.akshay.privy_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class MessageHistoryItem extends MessagePayload{

	private List<DeviceKeyDTO> deviceKeys = new ArrayList<>();

	public List<DeviceKeyDTO> getDeviceKeys() {
		return deviceKeys;
	}

	public void setDeviceKeys(List<DeviceKeyDTO> deviceKeys) {
		this.deviceKeys = deviceKeys;
	}
}
