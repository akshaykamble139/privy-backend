package com.akshay.privy_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String>{

	List<Message> findByChatIdOrderByCreatedAtAsc(UUID chatId);
	List<Message> findByChatIdAndMediaFileId(UUID chatId, UUID fileId);

	@Query("""
		    SELECT m FROM Message m
		    WHERE m.chat.id = :chatId
		      AND m.id < :cursorUlid
		    ORDER BY m.id DESC
		""")
		Page<Message> findByChatIdAndCursorUlid(
		    @Param("chatId") UUID chatId,
		    @Param("cursorUlid") String cursorUlid,
		    Pageable pageable
		);
}
