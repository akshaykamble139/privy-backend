package com.akshay.privy_backend.dto;

import java.io.Serializable;

public class LoginResponse implements Serializable{

	private static final long serialVersionUID = -5503095545031713444L;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
