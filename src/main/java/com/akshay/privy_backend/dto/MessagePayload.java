package com.akshay.privy_backend.dto;

import java.time.Instant;
import java.util.UUID;

public class MessagePayload {
	private String messageId;
	private UUID chatId;
    private String senderUsername;
    private String ciphertext;
    private UUID mediaFileId;
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
	public String getSenderUsername() {
		return senderUsername;
	}
	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}
	public String getCiphertext() {
		return ciphertext;
	}
	public UUID getMediaFileId() {
		return mediaFileId;
	}
	public void setMediaFileId(UUID mediaFileId) {
		this.mediaFileId = mediaFileId;
	}
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}
	public Instant getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
