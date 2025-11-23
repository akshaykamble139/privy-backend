package com.akshay.privy_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.MessageDelivery;

@Repository
public interface MessageDeliveryRepository extends JpaRepository<MessageDelivery, UUID> {

	List<MessageDelivery> findByMessageIdAndRecipientUserId(String messageId, UUID userId);

	Optional<MessageDelivery> findByMessageIdAndRecipientUserIdAndDeviceName(String id, UUID id2, String deviceName);

	@Query("""
			    SELECT md FROM MessageDelivery md
			    WHERE md.message.chat.id = :chatId
			      AND md.recipientUser.id = :userId
			      AND md.deviceName = :deviceName
			      AND md.message.id <= :lastMessageId
			      AND md.status <> 'READ'
			""")
	List<MessageDelivery> findUndeliveredOrUnreadUpTo(@Param("chatId") UUID chatId, @Param("userId") UUID userId,
			@Param("deviceName") String deviceName, @Param("lastMessageId") String lastMessageId);

	@Query("""
			    SELECT d FROM MessageDelivery d
			    WHERE d.recipientUser.id = :userId
			      AND d.deviceName = :deviceName
			      AND d.message.chat.id = :chatId
			      AND d.message.id >= :startUlid
			      AND d.message.id <= :endUlid
			    ORDER BY d.message.id ASC
			""")
	List<MessageDelivery> findDeliveriesForDeviceInRange(@Param("userId") UUID userId,
			@Param("deviceName") String deviceName, @Param("chatId") UUID chatId, @Param("startUlid") String startUlid,
			@Param("endUlid") String endUlid);

	@Query("SELECT m.id FROM Message m WHERE m.id = :id")
	Optional<String> existsMessageId(@Param("id") String id);
}
