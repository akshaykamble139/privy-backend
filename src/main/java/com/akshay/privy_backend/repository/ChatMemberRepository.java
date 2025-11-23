package com.akshay.privy_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.ChatMember;
import com.akshay.privy_backend.entity.MessageDelivery;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, UUID>{

	boolean existsByChatIdAndUserUsername(UUID chatId, String username);

	Optional<MessageDelivery> findByChatIdAndUserUsername(UUID chatId, String username);
}
