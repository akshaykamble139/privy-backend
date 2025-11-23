package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.akshay.privy_backend.dto.DeviceKeyDTO;
import com.akshay.privy_backend.dto.RangeAckRequest;
import com.akshay.privy_backend.dto.ReadThroughAckRequest;
import com.akshay.privy_backend.dto.SendMessageRequest;
import com.akshay.privy_backend.entity.AckType;
import com.akshay.privy_backend.entity.Chat;
import com.akshay.privy_backend.entity.ChatMember;
import com.akshay.privy_backend.entity.ChatType;
import com.akshay.privy_backend.entity.Device;
import com.akshay.privy_backend.entity.DevicePublicKey;
import com.akshay.privy_backend.entity.Message;
import com.akshay.privy_backend.entity.MessageDelivery;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.ChatMemberRepository;
import com.akshay.privy_backend.repository.ChatRepository;
import com.akshay.privy_backend.repository.MessageDeliveryRepository;
import com.akshay.privy_backend.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

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
	
	@Mock
	private MediaService mediaService;
	
	@Mock
	private ChatReadStateService chatReadStateService;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private MessageServiceImpl messageService;

	@Test
	public void processIncomingMessageInvalidRequestTest() {
		// null request
		boolean result = true;
		try {
			messageService.processIncomingMessage(null, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// null chat id
		SendMessageRequest request = new SendMessageRequest();
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// invalid cipher text
		request.setChatId(UUID.randomUUID());
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setCiphertext(" ");
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// invalid device name
		request.setCiphertext("encrypted text message");
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setDeviceName(" ");
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// invalid device keys list
		request.setDeviceName("iPhone");
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		request.setDeviceKeys(new ArrayList<>());
		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// invalid sender username
		DeviceKeyDTO dto1 = new DeviceKeyDTO();
		dto1.setDeviceName("iPhone1");
		dto1.setEncryptedKey("encryptedKey1");

		DeviceKeyDTO dto2 = new DeviceKeyDTO();
		dto2.setDeviceName("iPhone2");
		dto2.setEncryptedKey("encryptedKey2");

		request.getDeviceKeys().addAll(List.of(dto1, dto2));

		result = true;
		try {
			messageService.processIncomingMessage(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		result = true;
		try {
			messageService.processIncomingMessage(request, " ");
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// Unauthorized device
		when(deviceService.userOwnsDevice(anyString(), anyString())).thenReturn(false);
		result = true;
		try {
			messageService.processIncomingMessage(request, "akshay");
		} catch (SecurityException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		// Chat id incorrect
		when(deviceService.userOwnsDevice(anyString(), anyString())).thenReturn(true);
		result = true;
		try {
			messageService.processIncomingMessage(request, "akshay");
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
	}

	@Test
	public void processIncomingMessageCorrectRequestTest() {

		boolean result = true;
		SendMessageRequest request = new SendMessageRequest();
		request.setChatId(UUID.randomUUID());
		request.setCiphertext("encrypted text message");
		request.setDeviceName("iPhone1");
		DeviceKeyDTO dto1 = new DeviceKeyDTO();
		dto1.setDeviceName("iPhone1");
		dto1.setEncryptedKey("encryptedKey1");

		DeviceKeyDTO dto2 = new DeviceKeyDTO();
		dto2.setDeviceName("iPhone2");
		dto2.setEncryptedKey("encryptedKey2");

		request.setDeviceKeys(List.of(dto1, dto2));
		
		when(deviceService.userOwnsDevice(anyString(), anyString())).thenReturn(true);

		Device device1 = new Device();
		device1.setId(UUID.randomUUID());
		device1.setCreatedAt(Instant.now());
		device1.setDeviceName("iPhone1");

		DevicePublicKey publicKey1 = new DevicePublicKey();
		publicKey1.setId(UUID.randomUUID());
		publicKey1.setCreatedAt(Instant.now());
		publicKey1.setPublicKey("publicKey1");
		publicKey1.setDevice(device1);

		device1.setPublicKeys(List.of(publicKey1));

		User user1 = new User();
		user1.setId(UUID.randomUUID());
		user1.setCreatedAt(Instant.now());
		user1.setUsername("user1");
		user1.setDevices(List.of(device1));

		device1.setUser(user1);

		ChatMember member1 = new ChatMember();
		member1.setId(UUID.randomUUID());
		member1.setJoinedAt(Instant.now());
		member1.setUser(user1);

		Device device2 = new Device();
		device2.setId(UUID.randomUUID());
		device2.setCreatedAt(Instant.now());
		device2.setDeviceName("iPhone2");

		DevicePublicKey publicKey2 = new DevicePublicKey();
		publicKey2.setId(UUID.randomUUID());
		publicKey2.setCreatedAt(Instant.now());
		publicKey2.setPublicKey("publicKey2");
		publicKey2.setDevice(device2);

		device2.setPublicKeys(List.of(publicKey2));

		User user2 = new User();
		user2.setId(UUID.randomUUID());
		user2.setCreatedAt(Instant.now());
		user2.setUsername("user2");
		user2.setDevices(List.of(device2));

		device2.setUser(user2);

		ChatMember member2 = new ChatMember();
		member2.setId(UUID.randomUUID());
		member2.setJoinedAt(Instant.now());
		member2.setUser(user2);

		Chat chat = new Chat();
		chat.setId(request.getChatId());
		chat.setType(ChatType.DIRECT);
		chat.setCreatedAt(Instant.now());
		chat.setMembers(List.of(member1, member2));

		when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
		result = true;
		try {
			messageService.processIncomingMessage(request, "user1");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		when(deviceService.getUserDevices(anyString())).thenReturn(List.of(device1,device2));

		result = true;
		try {
			messageService.processIncomingMessage(request, "user1");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		request.setMediaFileId(UUID.randomUUID());

		result = true;
		try {
			messageService.processIncomingMessage(request, "user1");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		device1.setDeviceName("wrongDevice");

		when(deviceService.getUserDevices(anyString())).thenReturn(List.of(device1));

		result = true;
		try {
			messageService.processIncomingMessage(request, "user1");
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
	}
	
	@Test
	public void processRangeAckTest() {
		boolean result = true;
		
		try {
			messageService.processRangeAck(null, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		RangeAckRequest request = new RangeAckRequest();
		result = true;
		
		try {
			messageService.processRangeAck(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			messageService.processRangeAck(request, " ");
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String username = "akshay";
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setChatId(UUID.randomUUID());
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setDeviceName(" ");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setDeviceName("iPhone");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setStartMessageId("9uh9h9h");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setStartMessageId("11HZX5H0Q6HD8WET8K5X3A9KNV");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setEndMessageId("uy8h98uh9");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setEndMessageId("01HZX5H0Q6HD8WET8K5X3A9KNV");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setType(AckType.DELIVERED);
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setStartMessageId("01HZX5H0Q6HD8WET8K5X3A9KNV");
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (SecurityException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		User user = new User();
		user.setId(UUID.randomUUID());
		when(authService.findByUsername(anyString())).thenReturn(user);
		when(deviceService.userOwnsDevice(anyString(), anyString())).thenReturn(true);
		
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		Message message = new Message();
		message.setSender(user);
		
		when(messageRepository.findById(anyString())).thenReturn(Optional.of(message)).thenReturn(Optional.empty());
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		when(messageRepository.findById(anyString())).thenReturn(Optional.of(message)).thenReturn(Optional.of(message));;
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (SecurityException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		when(chatMemberRepository.existsByChatIdAndUserUsername(any(UUID.class),anyString())).thenReturn(true);
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		List<MessageDelivery> deliveries = new ArrayList<MessageDelivery>();
		MessageDelivery delivery = new MessageDelivery();
		delivery.setStatus(AckType.PENDING);
		
		deliveries.add(delivery);
		
		when(deliveryRepository.findDeliveriesForDeviceInRange(
				any(UUID.class),anyString(),any(UUID.class), anyString(), anyString())).thenReturn(deliveries);
		
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		user.setUsername(username);
		message.setSender(user);
		when(messageRepository.findById(anyString())).thenReturn(Optional.of(message)).thenReturn(Optional.of(message));;
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		delivery.setDeliveredAt(Instant.now());
		delivery.setStatus(AckType.FAILED);
		deliveries = List.of(delivery);
		
		when(deliveryRepository.findDeliveriesForDeviceInRange(
				any(UUID.class),anyString(),any(UUID.class), anyString(), anyString())).thenReturn(deliveries);
		
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);

		request.setType(AckType.FAILED);
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		delivery.setStatus(AckType.FAILED);
		deliveries = List.of(delivery);
		
		when(deliveryRepository.findDeliveriesForDeviceInRange(
				any(UUID.class),anyString(),any(UUID.class), anyString(), anyString())).thenReturn(deliveries);
		
		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);

		request.setType(AckType.PENDING);

		result = true;
		try {
			messageService.processRangeAck(request, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
	}
	
	@Test
	public void processReadThroughTest() {
		boolean result = true;
		
		try {
			messageService.processReadThrough(null, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		ReadThroughAckRequest request = new ReadThroughAckRequest();
		
		result = true;
		
		try {
			messageService.processReadThrough(request, null);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			messageService.processReadThrough(request, " ");
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String username = "akshay";
		result = true;
		try {
			messageService.processReadThrough(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setChatId(UUID.randomUUID());
		result = true;
		try {
			messageService.processReadThrough(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setLastMessageId("gygb8g8g");
		result = true;
		try {
			messageService.processReadThrough(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		request.setLastMessageId("01HZX5H0Q6HD8WET8K5X3A9KNV");
		result = true;
		try {
			messageService.processReadThrough(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		User user = new User();
		user.setUsername(username);
		
		Message message = new Message();
		message.setSender(user);
		
		
		when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));
		result = true;
		try {
			messageService.processReadThrough(request, username);
		} catch (IllegalArgumentException e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
	}
}
