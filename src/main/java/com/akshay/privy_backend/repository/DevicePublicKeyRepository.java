package com.akshay.privy_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.DevicePublicKey;

@Repository
public interface DevicePublicKeyRepository extends JpaRepository<DevicePublicKey, UUID>{

	List<DevicePublicKey> findByDeviceId(UUID deviceId);
	List<DevicePublicKey> findByDeviceUserId(UUID userId);
    List<DevicePublicKey> findByDeviceUserIdAndIsActive(UUID userId, boolean isActive); 
}
