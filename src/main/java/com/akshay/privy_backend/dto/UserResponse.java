package com.akshay.privy_backend.dto;

import java.io.Serializable;
import java.util.UUID;

public class UserResponse implements Serializable{
	
	private static final long serialVersionUID = -7020506268498620L;
	private UUID id;
	private String username;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}