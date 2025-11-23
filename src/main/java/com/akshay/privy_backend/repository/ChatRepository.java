package com.akshay.privy_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID>{

	@Query("SELECT c FROM Chat c "
			+ "JOIN c.members m1 JOIN c.members m2 "
			+ "WHERE c.type = 'DIRECT' "
			+ "AND m1.user.id = :id1 "
			+ "AND m2.user.id = :id2")
	Optional<Chat> findDirectChat(UUID id1, UUID id2);

	@Query("SELECT c FROM Chat c JOIN c.members m WHERE m.user.id = :userId")
	List<Chat> findByMember(UUID userId);
}
