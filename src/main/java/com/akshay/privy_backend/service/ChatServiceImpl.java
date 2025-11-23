package com.akshay.privy_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.akshay.privy_backend.dto.ChatDTO;
import com.akshay.privy_backend.dto.ChatMemberDevicesResponse;
import com.akshay.privy_backend.dto.CreateGroupRequest;
import com.akshay.privy_backend.dto.DeviceKeyDTO;
import com.akshay.privy_backend.dto.DeviceResponse;
import com.akshay.privy_backend.dto.MessageHistoryItem;
import com.akshay.privy_backend.dto.PaginatedMessagesResponse;
import com.akshay.privy_backend.entity.Chat;
import com.akshay.privy_backend.entity.ChatMember;
import com.akshay.privy_backend.entity.ChatType;
import com.akshay.privy_backend.entity.Message;
import com.akshay.privy_backend.entity.MessageDelivery;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.ChatMemberRepository;
import com.akshay.privy_backend.repository.ChatRepository;
import com.akshay.privy_backend.repository.MessageDeliveryRepository;
import com.akshay.privy_backend.repository.MessageRepository;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Autowired
	private ChatMemberRepository chatMemberRepository;

	@Autowired
	private MessageDeliveryRepository deliveryRepository;

	@Autowired
	private AuthService authService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Override
	public PaginatedMessagesResponse getChatMessages(
	        UUID chatId,
	        String username,
	        String deviceName,
	        String beforeUlid,
	        int limit
	) {
		if (chatId == null) {
			throw new IllegalArgumentException("Chat ID cannot be null");
		}
		else if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}
		else if (deviceName == null || deviceName.isBlank()) {
			throw new IllegalArgumentException("Device Name cannot be empty");
		}
		else if (limit <= 0) {
			throw new IllegalArgumentException("Limit should be positive");
		}
		else if (beforeUlid != null && !beforeUlid.matches("^[0-9A-HJKMNP-TV-Z]{26}$")) {
			throw new IllegalArgumentException("beforeUlid is not valid ULID string");			
		}
		
	    User user = authService.findByUsername(username);

	    boolean isMember = chatMemberRepository.existsByChatIdAndUserUsername(chatId, username);
        if (!isMember) {
            throw new SecurityException("Not a member of this chat");
        }

        String cursor = (beforeUlid == null) ? "7ZZZZZZZZZZZZZZZZZZZZZZZZZ" : beforeUlid;

        Pageable pageable = PageRequest.of(0, limit);

        Page<Message> page = messageRepository.findByChatIdAndCursorUlid(
                chatId,
                cursor,
                pageable
        );

	    List<MessageHistoryItem> result = new ArrayList<>();

	    for (Message m : page.getContent()) {

	        MessageDelivery delivery = deliveryRepository
	                .findByMessageIdAndRecipientUserIdAndDeviceName(m.getId(), user.getId(), deviceName)
	                .orElse(null);

	        MessageHistoryItem item = new MessageHistoryItem();
	        item.setMessageId(m.getId());
	        item.setChatId(chatId);
	        item.setSenderUsername(m.getSender().getUsername());
	        item.setCiphertext(m.getCiphertext());
	        item.setTimestamp(m.getCreatedAt());
	        item.setMediaFileId(m.getMediaFileId());

	        DeviceKeyDTO dk = new DeviceKeyDTO();
	        dk.setDeviceName(deviceName);
	        if (delivery != null) {
	            dk.setEncryptedKey(delivery.getEncryptedKey());
	        }

	        item.getDeviceKeys().add(dk);

	        result.add(item);
	    }

	    PaginatedMessagesResponse response = new PaginatedMessagesResponse();
	    response.setMessages(result);

	    if (!page.isEmpty()) {
	        Message last = page.getContent().get(page.getContent().size() - 1);
	        response.setNextCursorTimestamp(last.getCreatedAt());
	        response.setNextCursorId(last.getId());
	        response.setHasMore(page.hasNext());
	    }

	    return response;
	}
	
	@Override
	public ChatDTO startDirectChat(String creatorUsername, String otherUsername) {
		
		if (creatorUsername == null || creatorUsername.isBlank()) {
			throw new IllegalArgumentException("Creator Username cannot be empty");
		}
		else if (otherUsername == null || otherUsername.isBlank()) {
			throw new IllegalArgumentException("Other Username cannot be empty");
		}

	    User a = authService.findByUsername(creatorUsername);
	    User b = authService.findByUsername(otherUsername);

	    Optional<Chat> existing = chatRepository.findDirectChat(a.getId(), b.getId());
	    Chat chat;

	    if (existing.isPresent()) {
	        chat = existing.get();
	    } else {
	        chat = new Chat();
	        chat.setType(ChatType.DIRECT);
	        chatRepository.save(chat);
	        
	        ChatMember member1 = new ChatMember();
	        member1.setChat(chat);
	        member1.setUser(a);
	        
	        ChatMember member2 = new ChatMember();
	        member2.setChat(chat);
	        member2.setUser(b);
	        
	        chatMemberRepository.saveAll(List.of(member1,member2));
	    }

	    ChatDTO dto = new ChatDTO();
	    dto.setChatId(chat.getId());
	    dto.setChatType(chat.getType().toString());
	    dto.setMembers(List.of(a.getUsername(), b.getUsername()));

	    return dto;
	}
	
	@Override
	public ChatDTO createGroupChat(String creatorUsername, CreateGroupRequest req) {
		if (creatorUsername == null || creatorUsername.isBlank()) {
			throw new IllegalArgumentException("Creator Username cannot be empty");
		}
		else if (req == null) {
			throw new IllegalArgumentException("CreateGroupRequest cannot be null");			
		}
		else if (req.getName() == null || req.getName().isBlank()) {
			throw new IllegalArgumentException("Group name cannot be empty");			
		}
		else if (req.getMembers().isEmpty()) {
			throw new IllegalArgumentException("Group members list cannot be empty");						
		}
		else if (req.getMembers().stream().anyMatch(mem -> mem.isBlank())) {
			throw new IllegalArgumentException("Group members name cannot be empty");									
		}
				
	    User creator = authService.findByUsername(creatorUsername);

	    Chat chat = new Chat();
	    chat.setType(ChatType.GROUP);
	    chat.setGroupName(req.getName());
	    chatRepository.save(chat);
	    
	    ChatMember member = new ChatMember();
        member.setChat(chat);
        member.setUser(creator);

        List<ChatMember> members = new ArrayList<>();
        
        members.add(member);
        
	    for (String username : req.getMembers()) {
	        User u = authService.findByUsername(username);
	        ChatMember mem = new ChatMember();
	        mem.setChat(chat);
	        mem.setUser(u);
	        
	        members.add(mem);
	    }
	    
	    chatMemberRepository.saveAll(members);

	    ChatDTO dto = new ChatDTO();
	    dto.setChatId(chat.getId());
	    dto.setChatType(ChatType.GROUP.toString());

	    List<String> allMembers = new ArrayList<>();
	    allMembers.add(creator.getUsername());
	    allMembers.addAll(req.getMembers());
	    dto.setMembers(allMembers);

	    return dto;
	}

	@Override
	public List<ChatDTO> getChatsForUser(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}
	    User user = authService.findByUsername(username);

	    List<Chat> chats = chatRepository.findByMember(user.getId());

	    List<ChatDTO> result = new ArrayList<>();

	    for (Chat chat : chats) {
	        ChatDTO dto = new ChatDTO();
	        dto.setChatId(chat.getId());
	        dto.setChatType(chat.getType().toString());

	        List<String> memberNames = chat.getMembers()
	                .stream()
	                .map(m -> m.getUser().getUsername())
	                .toList();

	        dto.setMembers(memberNames);

	        result.add(dto);
	    }

	    return result;
	}
	
	@Override
    public List<ChatMemberDevicesResponse> getChatDevices(UUID chatId) {
		
		if (chatId == null) {
			throw new IllegalArgumentException("Chat ID cannot be null");
		}

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        List<ChatMemberDevicesResponse> result = new ArrayList<>();

        for (ChatMember member : chat.getMembers()) {

            List<DeviceResponse> deviceResponses = deviceService.getAllPublicKeysByUsername(member.getUser().getUsername());

            ChatMemberDevicesResponse dto = new ChatMemberDevicesResponse();
            dto.setUsername(member.getUser().getUsername());
            dto.setDevices(deviceResponses);
            result.add(dto);
        }

        return result;
    }
}
