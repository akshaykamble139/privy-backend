package com.akshay.privy_backend.service;

import java.util.List;
import java.util.UUID;

import com.akshay.privy_backend.dto.ChatDTO;
import com.akshay.privy_backend.dto.ChatMemberDevicesResponse;
import com.akshay.privy_backend.dto.CreateGroupRequest;
import com.akshay.privy_backend.dto.PaginatedMessagesResponse;

public interface ChatService {

	ChatDTO startDirectChat(String creatorUsername, String otherUsername);
	ChatDTO createGroupChat(String creatorUsername, CreateGroupRequest req);
	List<ChatDTO> getChatsForUser(String username);
	List<ChatMemberDevicesResponse> getChatDevices(UUID chatId);
	PaginatedMessagesResponse getChatMessages(UUID chatId, String username, String deviceName, String beforeUlid, int limit);
}
