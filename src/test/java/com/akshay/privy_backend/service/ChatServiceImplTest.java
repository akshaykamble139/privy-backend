package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.akshay.privy_backend.dto.ChatDTO;
import com.akshay.privy_backend.dto.CreateGroupRequest;
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

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private ChatMemberRepository chatMemberRepository;

	@Mock
	private MessageDeliveryRepository deliveryRepository;

	@Mock
	private AuthService authService;

	@Mock
	private DeviceService deviceService;

	@InjectMocks
	private ChatServiceImpl chatService;

	@Test
	public void getChatMessagesTest() {

		boolean result = true;

		try {
			chatService.getChatMessages(null, null, null, null, 0);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		UUID chatId = UUID.randomUUID();

		result = true;

		try {
			chatService.getChatMessages(chatId, null, null, null, 0);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.getChatMessages(chatId, " ", null, null, 0);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		String username = "akshay";
		result = true;

		try {
			chatService.getChatMessages(chatId, username, null, null, 0);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.getChatMessages(chatId, username, " ", null, 0);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);
		String deviceName = "iPhone";
		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, null, -5);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, "uf86yf8fiygiyt", 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		User user = new User();

		user.setId(UUID.randomUUID());
		user.setUsername("user1");

		when(authService.findByUsername(anyString())).thenReturn(user);

		when(chatMemberRepository.existsByChatIdAndUserUsername(any(UUID.class), anyString())).thenReturn(false);

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, null, 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, "01HZX5H0Q6HD8WET8K5X3A9KNV", 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(false, result);

		when(chatMemberRepository.existsByChatIdAndUserUsername(any(UUID.class), anyString())).thenReturn(true);

		Pageable pageable = PageRequest.of(0, 30);

		when(messageRepository.findByChatIdAndCursorUlid(any(UUID.class), anyString(), any(Pageable.class)))
				.thenReturn(Page.empty(pageable));

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, null, 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(true, result);

		Message message = new Message();
		message.setId("8yhfhfgh");
		message.setSender(user);

		List<Message> messages = List.of(message);

		Page<Message> page = new PageImpl<>(messages, pageable, messages.size());
		when(messageRepository.findByChatIdAndCursorUlid(any(UUID.class), anyString(), any(Pageable.class)))
				.thenReturn(page);

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, null, 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(true, result);

		result = true;

		messages = List.of(message);

		page = new PageImpl<>(messages, pageable, messages.size());
		when(messageRepository.findByChatIdAndCursorUlid(any(UUID.class), anyString(), any(Pageable.class)))
				.thenReturn(page);

		MessageDelivery delivery = new MessageDelivery();
		delivery.setEncryptedKey("encryptedKey1");

		when(deliveryRepository.findByMessageIdAndRecipientUserIdAndDeviceName(anyString(), any(UUID.class),
				anyString())).thenReturn(Optional.of(delivery));

		result = true;

		try {
			chatService.getChatMessages(chatId, username, deviceName, "01HZX5H0Q6HD8WET8K5X3A9KNV", 30);
		} catch (Exception e) {
			result = false;
		}

		Assertions.assertEquals(true, result);
	}

	@Test
	public void startDirectChatTest() {
		boolean result = true;

		try {
			chatService.startDirectChat(null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.startDirectChat(" ", null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		String creatorUsername = "akshay";

		result = true;

		try {
			chatService.startDirectChat(creatorUsername, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.startDirectChat(creatorUsername, " ");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		String otherUser = "bob";

		User user = new User();
		user.setUsername(creatorUsername);
		user.setId(UUID.randomUUID());

		when(authService.findByUsername(anyString())).thenReturn(user);
		result = true;

		try {
			chatService.startDirectChat(creatorUsername, otherUser);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);

		Chat chat = new Chat();
		chat.setType(ChatType.DIRECT);

		when(chatRepository.findDirectChat(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(chat));

		result = true;

		try {
			chatService.startDirectChat(creatorUsername, otherUser);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		Assertions.assertEquals(true, result);
	}

	@Test
	public void createGroupChatTest() {
		boolean result = true;

		try {
			chatService.createGroupChat(null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		result = true;

		try {
			chatService.createGroupChat(" ", null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		String creatorUsername = "akshay";

		result = true;

		try {
			chatService.createGroupChat(creatorUsername, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		CreateGroupRequest request = new CreateGroupRequest();
		result = true;

		try {
			chatService.createGroupChat(creatorUsername, request);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setName(" ");
		result = true;

		try {
			chatService.createGroupChat(creatorUsername, request);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setName("group1");
		result = true;

		try {
			chatService.createGroupChat(creatorUsername, request);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setMembers(List.of(" "));
		result = true;

		try {
			chatService.createGroupChat(creatorUsername, request);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setMembers(List.of("tom"));
		User user = new User();
		user.setUsername("bob");

		when(authService.findByUsername(anyString())).thenReturn(user);

		result = true;

		try {
			chatService.createGroupChat(creatorUsername, request);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
	}
	
	@Test
	public void getChatsForUserTest() {
		boolean result = true;
		
		try {
			chatService.getChatsForUser(null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		
		try {
			chatService.getChatsForUser(" ");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		User user = new User();
		user.setId(UUID.randomUUID());
		
		when(authService.findByUsername(anyString())).thenReturn(user);
		
		List<Chat> chats = new ArrayList<Chat>();
		
		Chat chat = new Chat();
		chat.setType(ChatType.DIRECT);
		
		ChatMember chatMember = new ChatMember();
		chatMember.setChat(chat);
		chatMember.setUser(user);
		
		chat.setMembers(List.of(chatMember));
		
		chats.add(chat);
		
		when(chatRepository.findByMember(any(UUID.class))).thenReturn(chats);
		
		List<ChatDTO> results = chatService.getChatsForUser("akshay");
		
		Assertions.assertEquals(1, results.size());
	}
	
	@Test
	public void getChatDevicesTest() {
		boolean result = true;
		
		try {
			chatService.getChatDevices(null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		
		try {
			chatService.getChatDevices(UUID.randomUUID());
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		User user = new User();
		user.setId(UUID.randomUUID());
				
		Chat chat = new Chat();
		chat.setType(ChatType.DIRECT);
		
		ChatMember chatMember = new ChatMember();
		chatMember.setChat(chat);
		chatMember.setUser(user);
		
		chat.setMembers(List.of(chatMember));
				
		when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
		result = true;
		
		try {
			chatService.getChatDevices(UUID.randomUUID());
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		Assertions.assertEquals(true, result);
	}

}
