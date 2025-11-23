package com.akshay.privy_backend.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akshay.privy_backend.repository.MediaFileRepository;

@Component
@EnableScheduling
public class OrphanMediaCleanupTask {

    @Autowired
    private MediaFileRepository mediaRepo;

    @Scheduled(fixedRate = 600_000)
    public void cleanup() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(10));
        mediaRepo.deleteOrphans(cutoff);
    }
}
