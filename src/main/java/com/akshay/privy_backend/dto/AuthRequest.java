package com.akshay.privy_backend.dto;

import java.io.Serializable;

public class AuthRequest implements Serializable{

	private static final long serialVersionUID = -2142775467054368701L;
	private String username;
	private String password;
	private String deviceName;
	private String publicKey;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
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