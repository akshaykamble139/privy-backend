package com.akshay.privy_backend.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Table(name = "devices")
@Entity
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String deviceName;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant lastSeenAt;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevicePublicKey> publicKeys = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getLastSeenAt() {
		return lastSeenAt;
	}

	public void setLastSeenAt(Instant lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DevicePublicKey> getPublicKeys() {
		return publicKeys;
	}

	public void setPublicKeys(List<DevicePublicKey> publicKeys) {
		this.publicKeys = publicKeys;
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", user=" + user + ", deviceName=" + deviceName + ", createdAt=" + createdAt
				+ ", lastSeenAt=" + lastSeenAt + ", status=" + status + ", publicKeys=" + publicKeys + "]";
	}
 
}
