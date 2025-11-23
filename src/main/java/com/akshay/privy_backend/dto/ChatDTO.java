package com.akshay.privy_backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatDTO {
    private UUID chatId;
    private String chatType;
    private List<String> members = new ArrayList<>();
	public UUID getChatId() {
		return chatId;
	}
	public void setChatId(UUID chatId) {
		this.chatId = chatId;
	}
	public String getChatType() {
		return chatType;
	}
	public void setChatType(String chatType) {
		this.chatType = chatType;
	}
	public List<String> getMembers() {
		return members;
	}
	public void setMembers(List<String> members) {
		this.members = members;
	}
}
