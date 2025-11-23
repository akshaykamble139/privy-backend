package com.akshay.privy_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequest {
    private String name;
    private List<String> members = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getMembers() {
		return members;
	}
	public void setMembers(List<String> members) {
		this.members = members;
	}
}

