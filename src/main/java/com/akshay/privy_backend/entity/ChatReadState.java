package com.akshay.privy_backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_read_state", uniqueConstraints = @UniqueConstraint(columnNames = { "chat_id", "user_id" }))
public class ChatReadState {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "chat_id", nullable = false)
	private Chat chat;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

    @Column(name = "last_read_message_id", nullable = false)
	private String lastReadMessageId;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLastReadMessageId() {
		return lastReadMessageId;
	}

	public void setLastReadMessageId(String lastReadMessageId) {
		this.lastReadMessageId = lastReadMessageId;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
