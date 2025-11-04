package com.akshay.privy_backend.dto;

import java.io.Serializable;

public class RegisterRequest implements Serializable{

	private static final long serialVersionUID = 3888548213319551427L;
	private String username;
	private String password;
	
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
}
