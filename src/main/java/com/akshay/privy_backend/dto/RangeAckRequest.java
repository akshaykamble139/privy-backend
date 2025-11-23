package com.akshay.privy_backend.dto;

import java.util.UUID;

import com.akshay.privy_backend.entity.AckType;

public class RangeAckRequest {
	private UUID chatId;
    private String deviceName;
    private String startMessageId;
    private String endMessageId;
    private AckType type;
	public UUID getChatId() {
		return chatId;
	}
	public void setChatId(UUID chatId) {
		this.chatId = chatId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getStartMessageId() {
		return startMessageId;
	}
	public void setStartMessageId(String startMessageId) {
		this.startMessageId = startMessageId;
	}
	public String getEndMessageId() {
		return endMessageId;
	}
	public void setEndMessageId(String endMessageId) {
		this.endMessageId = endMessageId;
	}
	public AckType getType() {
		return type;
	}
	public void setType(AckType type) {
		this.type = type;
	}
}
