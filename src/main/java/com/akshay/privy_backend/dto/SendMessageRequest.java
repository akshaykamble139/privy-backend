package com.akshay.privy_backend.dto;

import java.util.List;
import java.util.UUID;

public class SendMessageRequest {
    private UUID chatId;
    private String ciphertext;
    private List<DeviceKeyDTO> deviceKeys;
    private String deviceName;
    private UUID mediaFileId;
	public UUID getChatId() {
		return chatId;
	}
	public void setChatId(UUID chatId) {
		this.chatId = chatId;
	}
	public String getCiphertext() {
		return ciphertext;
	}
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}
	public List<DeviceKeyDTO> getDeviceKeys() {
		return deviceKeys;
	}
	public void setDeviceKeys(List<DeviceKeyDTO> deviceKeys) {
		this.deviceKeys = deviceKeys;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public UUID getMediaFileId() {
		return mediaFileId;
	}
	public void setMediaFileId(UUID mediaFileId) {
		this.mediaFileId = mediaFileId;
	}
}