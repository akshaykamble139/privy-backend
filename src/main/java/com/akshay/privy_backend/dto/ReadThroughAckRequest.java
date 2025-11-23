package com.akshay.privy_backend.dto;

import java.util.UUID;

public class ReadThroughAckRequest {
	private UUID chatId;
    private String lastMessageId; 
	public UUID getChatId() {
		return chatId;
	}
	public void setChatId(UUID chatId) {
		this.chatId = chatId;
	}
	public String getLastMessageId() {
		return lastMessageId;
	}
	public void setLastMessageId(String lastMessageId) {
		this.lastMessageId = lastMessageId;
	}
}
