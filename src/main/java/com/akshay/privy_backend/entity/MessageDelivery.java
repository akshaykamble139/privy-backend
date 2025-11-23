package com.akshay.privy_backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "message_deliveries")
public class MessageDelivery {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne 
    @JoinColumn(nullable = false, name = "message_id")
    private Message message;

    @ManyToOne @JoinColumn(nullable = false)
    private User recipientUser;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AckType status = AckType.PENDING; 

    @Column
    private Instant deliveredAt;
    
    @Column(nullable = false)
    private String deviceName;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedKey;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public User getRecipientUser() {
		return recipientUser;
	}

	public void setRecipientUser(User recipientUser) {
		this.recipientUser = recipientUser;
	}

	public AckType getStatus() {
		return status;
	}

	public void setStatus(AckType status) {
		this.status = status;
	}

	public Instant getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(Instant deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getEncryptedKey() {
		return encryptedKey;
	}

	public void setEncryptedKey(String encryptedKey) {
		this.encryptedKey = encryptedKey;
	}
}
