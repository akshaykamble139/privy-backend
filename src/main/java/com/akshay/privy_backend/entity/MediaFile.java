package com.akshay.privy_backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_files")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] ciphertext;

    @Column
    private String filename;

    @Column
    private String mimeType;

    @Column
    private long size;

    @Column
    private String hash; 

    @Column
    private Instant createdAt;
    
    @Column(nullable = false)
    private boolean attachedToMessage = false;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public byte[] getCiphertext() {
		return ciphertext;
	}

	public void setCiphertext(byte[] ciphertext) {
		this.ciphertext = ciphertext;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isAttachedToMessage() {
		return attachedToMessage;
	}

	public void setAttachedToMessage(boolean attachedToMessage) {
		this.attachedToMessage = attachedToMessage;
	}
}
