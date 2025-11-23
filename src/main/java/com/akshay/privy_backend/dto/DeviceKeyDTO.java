package com.akshay.privy_backend.dto;

public class DeviceKeyDTO {
    private String deviceName;
    private String encryptedKey;
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getEncryptedKey() {
		return encryptedKey;
	}
	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
}
