package com.akshay.privy_backend.service;

import java.util.UUID;

public interface ChatReadStateService {
    public String updateReadState(UUID chatId, String username, String newMessageId);
}
