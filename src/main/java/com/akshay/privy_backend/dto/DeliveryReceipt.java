package com.akshay.privy_backend.dto;

import java.time.Instant;
import java.util.UUID;

public class DeliveryReceipt {

	private String messageId;
    private UUID chatId;
    private String recipientUsername;
    private String deviceName;
    private String type;
    private Instant timestamp;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public UUID getChatId() {
		return chatId;
	}
	public void setChatId(UUID chatId) {
		this.chatId = chatId;
	}
	public String getRecipientUsername() {
		return recipientUsername;
	}
	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Instant getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
