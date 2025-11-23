package com.akshay.privy_backend.repository;

import com.akshay.privy_backend.entity.ChatReadState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatReadStateRepository extends JpaRepository<ChatReadState, UUID> {
    Optional<ChatReadState> findByChatIdAndUserId(UUID chatId, UUID userId);
}