package com.akshay.privy_backend.service;

import com.akshay.privy_backend.dto.RangeAckRequest;
import com.akshay.privy_backend.dto.ReadThroughAckRequest;
import com.akshay.privy_backend.dto.SendMessageRequest;

public interface MessageService {
    void processIncomingMessage(SendMessageRequest request, String senderUsername);
    void processRangeAck(RangeAckRequest req, String username);
    void processReadThrough(ReadThroughAckRequest req, String username);
}
