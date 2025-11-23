package com.akshay.privy_backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.akshay.privy_backend.dto.RangeAckRequest;
import com.akshay.privy_backend.dto.SendMessageRequest;
import com.akshay.privy_backend.service.MessageService;

@Controller
public class MessageController {

	@Autowired
	private MessageService messageService;
	
    @MessageMapping("/send")
    public void handleSend(SendMessageRequest request, Principal principal) {
        messageService.processIncomingMessage(request, principal.getName());
    }
    
    @MessageMapping("/ack/range")
    public void handleRangeAck(RangeAckRequest req, Principal principal) {
        messageService.processRangeAck(req, principal.getName());
    }

	
}