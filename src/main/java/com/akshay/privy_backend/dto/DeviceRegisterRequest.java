package com.akshay.privy_backend.dto;

import java.io.Serializable;

public class DeviceRegisterRequest implements Serializable{

	private static final long serialVersionUID = -4820556623336580513L;
	private String deviceName;
	private String publicKey;
	
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}
