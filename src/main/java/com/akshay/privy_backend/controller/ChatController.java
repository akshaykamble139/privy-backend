package com.akshay.privy_backend.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akshay.privy_backend.dto.ChatDTO;
import com.akshay.privy_backend.dto.CreateGroupRequest;
import com.akshay.privy_backend.dto.PaginatedMessagesResponse;
import com.akshay.privy_backend.dto.StartChatRequest;
import com.akshay.privy_backend.service.ChatService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
        
    @GetMapping("/{chatId}/messages")
    public PaginatedMessagesResponse getChatMessages(
            @PathVariable UUID chatId,
            @RequestParam(required = false) String beforeUlid,
            @RequestParam(defaultValue = "30") int limit,
            @RequestHeader("X-Device-Name") String deviceName,
            Principal principal) {

        return chatService.getChatMessages(chatId, principal.getName(), deviceName, beforeUlid, limit);
    }
    
    @PostMapping("/start")
    public ChatDTO startDirectChat(@RequestBody StartChatRequest request, Principal principal) {
        return chatService.startDirectChat(principal.getName(), request.getUsername());
    }
    
    @PostMapping("/create-group")
    public ChatDTO createGroup(@RequestBody CreateGroupRequest request, Principal principal) {
        return chatService.createGroupChat(principal.getName(), request);
    }

    @GetMapping
    public List<ChatDTO> getMyChats(Principal principal) {
        return chatService.getChatsForUser(principal.getName());
    }

}

