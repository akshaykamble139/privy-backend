package com.akshay.privy_backend.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "messages",
    indexes = {
        @Index(name = "idx_chat_id_ulid", columnList = "chat_id, id")
    }
)
public class Message {
	
	@SuppressWarnings("deprecation")
	@Id
	@GeneratedValue(generator = "ulid")
	@GenericGenerator(
	        name = "ulid",
	        type = com.akshay.privy_backend.entity.UlidGenerator.class
	)
	@Column(nullable = false, updatable = false)
	private String id;

    @ManyToOne 
    @JoinColumn(nullable = false)
    private Chat chat;

    @ManyToOne 
    @JoinColumn(nullable = false)
    private User sender;

    @ManyToOne 
    @JoinColumn
    private Device senderDevice;

    @Column(nullable = false, length = 5000, columnDefinition = "TEXT")
    private String ciphertext;
    
    @Column(nullable = true)
    private UUID mediaFileId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageDelivery> deliveries = new ArrayList<>();
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public Device getSenderDevice() {
		return senderDevice;
	}

	public void setSenderDevice(Device senderDevice) {
		this.senderDevice = senderDevice;
	}

	public String getCiphertext() {
		return ciphertext;
	}

	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}

	public UUID getMediaFileId() {
		return mediaFileId;
	}

	public void setMediaFileId(UUID mediaFileId) {
		this.mediaFileId = mediaFileId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public List<MessageDelivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<MessageDelivery> deliveries) {
		this.deliveries = deliveries;
	}
}

