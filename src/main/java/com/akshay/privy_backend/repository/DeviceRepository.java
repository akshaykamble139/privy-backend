package com.akshay.privy_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID>{

	List<Device> findByUserId(UUID userId);
	List<Device> findByUserUsername(String username);
	Optional<Device> findByUserUsernameAndDeviceName(String username, String deviceName);
}
