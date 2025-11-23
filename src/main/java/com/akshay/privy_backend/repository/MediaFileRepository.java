package com.akshay.privy_backend.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.akshay.privy_backend.entity.MediaFile;

import jakarta.transaction.Transactional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
    Optional<MediaFile> findByHash(String hash);
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM MediaFile f 
        WHERE f.attachedToMessage = false 
          AND f.createdAt < :cutoff
    """)
    void deleteOrphans(@Param("cutoff") Instant cutoff);

}

