package com.akshay.privy_backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.akshay.privy_backend.dto.DeliveryReceipt;
import com.akshay.privy_backend.dto.DeviceKeyDTO;
import com.akshay.privy_backend.dto.MessagePayload;
import com.akshay.privy_backend.dto.RangeAckRequest;
import com.akshay.privy_backend.dto.ReadThroughAckRequest;
import com.akshay.privy_backend.dto.SendMessageRequest;
import com.akshay.privy_backend.entity.AckType;
import com.akshay.privy_backend.entity.Chat;
import com.akshay.privy_backend.entity.ChatMember;
import com.akshay.privy_backend.entity.Device;
import com.akshay.privy_backend.entity.Message;
import com.akshay.privy_backend.entity.MessageDelivery;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.ChatMemberRepository;
import com.akshay.privy_backend.repository.ChatRepository;
import com.akshay.privy_backend.repository.MessageDeliveryRepository;
import com.akshay.privy_backend.repository.MessageRepository;

import jakarta.transaction.Transactional;

@Service
public class MessageServiceImpl implements MessageService {

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
	
	@Autowired
	private MediaService mediaService;

	@Autowired
	private ChatReadStateService chatReadStateService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Transactional
	@Override
	public void processIncomingMessage(SendMessageRequest request, String senderUsername) {

		if (request == null)
			throw new IllegalArgumentException("Request cannot be null");
		if (request.getChatId() == null)
			throw new IllegalArgumentException("Chat ID cannot be null");
		if (request.getCiphertext() == null || request.getCiphertext().isBlank())
			throw new IllegalArgumentException("Ciphertext cannot be null");
		if (request.getDeviceName() == null || request.getDeviceName().isBlank())
			throw new IllegalArgumentException("Device name cannot be null");
		if (request.getDeviceKeys() == null || request.getDeviceKeys().isEmpty())
			throw new IllegalArgumentException("Device key list cannot be empty");
		if (senderUsername == null || senderUsername.isBlank())
			throw new IllegalArgumentException("Sender username cannot be empty");

		if (!deviceService.userOwnsDevice(senderUsername, request.getDeviceName())) {
			throw new SecurityException("Unauthorized device: " + request.getDeviceName());
		}

		Chat chat = chatRepository.findById(request.getChatId())
				.orElseThrow(() -> new IllegalArgumentException("Chat not found"));

		User sender = authService.findByUsername(senderUsername);

		Message message = new Message();
		message.setChat(chat);
		message.setSender(sender);
		message.setCiphertext(request.getCiphertext());
		message.setCreatedAt(Instant.now());

		if (request.getMediaFileId() != null) {
			message.setMediaFileId(request.getMediaFileId());
		}

		messageRepository.save(message);
		
		if (request.getMediaFileId() != null) {
		    mediaService.markFileAttached(request.getMediaFileId());
		}

		List<MessageDelivery> deliveries = new ArrayList<>();

		Map<String, String> keyMap = request.getDeviceKeys().stream()
				.collect(Collectors.toMap(DeviceKeyDTO::getDeviceName, DeviceKeyDTO::getEncryptedKey));

		for (ChatMember member : chat.getMembers()) {
			String recipientUsername = member.getUser().getUsername();
			List<Device> recipientDevices = deviceService.getUserDevices(recipientUsername);

			for (Device device : recipientDevices) {
				String deviceName = device.getDeviceName();

				if (!keyMap.containsKey(deviceName)) {
					throw new IllegalArgumentException(
							"Missing encrypted key for device: " + deviceName + " of user: " + recipientUsername);
				}
			}

			for (Device device : recipientDevices) {

				String deviceName = device.getDeviceName();
				String encryptedKey = keyMap.get(deviceName);

				MessageDelivery delivery = new MessageDelivery();
				delivery.setMessage(message);
				delivery.setRecipientUser(member.getUser());
				delivery.setDeviceName(deviceName);
				delivery.setEncryptedKey(encryptedKey);

				if (delivery.getRecipientUser().getUsername().equals(senderUsername)
						&& deviceName.equals(request.getDeviceName())) {
					delivery.setStatus(AckType.DELIVERED);
					delivery.setDeliveredAt(Instant.now());
				}

				deliveries.add(delivery);
			}
		}

		deliveryRepository.saveAll(deliveries);

		MessagePayload payload = new MessagePayload();
		payload.setMessageId(message.getId());
		payload.setChatId(request.getChatId());
		payload.setSenderUsername(senderUsername);
		payload.setCiphertext(request.getCiphertext());
		payload.setTimestamp(message.getCreatedAt());
		payload.setMediaFileId(message.getMediaFileId());

		for (ChatMember member : chat.getMembers()) {
			messagingTemplate.convertAndSendToUser(member.getUser().getUsername(), "/queue/messages", payload);
		}
	}

	@Transactional
	@Override
	public void processRangeAck(RangeAckRequest req, String username) {
		if (req == null)
			throw new IllegalArgumentException("Request cannot be null");
		if (username == null || username.isBlank())
			throw new IllegalArgumentException("Username cannot be empty");
		if (req.getChatId() == null)
			throw new IllegalArgumentException("Chat ID cannot be null");
		if (req.getDeviceName() == null || req.getDeviceName().isBlank())
			throw new IllegalArgumentException("Device name cannot be null");
		if (req.getStartMessageId() == null || !req.getStartMessageId().matches("^[0-9A-HJKMNP-TV-Z]{26}$"))
			throw new IllegalArgumentException("Start message ID is not valid ULID string");
		if (req.getEndMessageId() == null || !req.getEndMessageId().matches("^[0-9A-HJKMNP-TV-Z]{26}$"))
			throw new IllegalArgumentException("End message ID is not valid ULID string");
		if (req.getType() == null)
			throw new IllegalArgumentException("Ack type cannot be null");

		if (req.getStartMessageId().compareTo(req.getEndMessageId()) > 0) {
			throw new IllegalArgumentException("startMessageId must be <= endMessageId");
		}

		User user = authService.findByUsername(username);

		if (!deviceService.userOwnsDevice(username, req.getDeviceName())) {
			throw new SecurityException("Device does not belong to user");
		}

		Message m1 = messageRepository.findById(req.getStartMessageId())
				.orElseThrow(() -> new IllegalArgumentException("start message not found"));
		if (messageRepository.findById(req.getEndMessageId()).isEmpty()) {
			throw new IllegalArgumentException("end message not found");
		}

		boolean isMember = chatMemberRepository.existsByChatIdAndUserUsername(req.getChatId(), username);
		if (!isMember) {
			throw new SecurityException("User is not a member of the chat");
		}

		List<MessageDelivery> deliveries = deliveryRepository.findDeliveriesForDeviceInRange(user.getId(),
				req.getDeviceName(), req.getChatId(), req.getStartMessageId(), req.getEndMessageId());

		if (deliveries.isEmpty()) {
			return;
		}

		Instant now = Instant.now();
		List<MessageDelivery> changed = new ArrayList<>();
		for (MessageDelivery d : deliveries) {
			if (req.getType() == AckType.DELIVERED) {
				if (d.getStatus() != AckType.DELIVERED) {
					d.setStatus(AckType.DELIVERED);
					if (d.getDeliveredAt() == null)
						d.setDeliveredAt(now);
					changed.add(d);
				}
			} else if (req.getType() == AckType.FAILED) {
				if (d.getStatus() != AckType.FAILED) {
					d.setStatus(AckType.FAILED);
					changed.add(d);
				}
			}
		}

		if (!changed.isEmpty()) {
			deliveryRepository.saveAll(changed);
		}

		String sender = m1.getSender().getUsername();

		if (sender != null && (req.getType() == AckType.DELIVERED || req.getType() == AckType.FAILED)) {
			DeliveryReceipt receipt = new DeliveryReceipt();
			receipt.setMessageId(req.getEndMessageId());
			receipt.setChatId(req.getChatId());
			receipt.setRecipientUsername(username);
			receipt.setDeviceName(req.getDeviceName());
			receipt.setType(req.getType().name());
			receipt.setTimestamp(now);

			messagingTemplate.convertAndSendToUser(sender, "/queue/receipts", receipt);
		}
	}

	@Transactional
	@Override
	public void processReadThrough(ReadThroughAckRequest req, String username) {
		if (req == null)
			throw new IllegalArgumentException("request cannot be null");
		if (username == null || username.isBlank())
			throw new IllegalArgumentException("Username cannot be empty");
		else if (req.getChatId() == null)
			throw new IllegalArgumentException("Chat ID cannot be null");
		else if (req.getLastMessageId() == null || !req.getLastMessageId().matches("^[0-9A-HJKMNP-TV-Z]{26}$")) {
			throw new IllegalArgumentException("Last message ID is not valid ULID string");
		}

		chatReadStateService.updateReadState(req.getChatId(), username, req.getLastMessageId());

		String sender = messageRepository.findById(req.getLastMessageId()).map(m -> m.getSender().getUsername())
				.orElse(null);

		if (sender != null) {
			DeliveryReceipt readReceipt = new DeliveryReceipt();
			readReceipt.setMessageId(req.getLastMessageId());
			readReceipt.setChatId(req.getChatId());
			readReceipt.setRecipientUsername(username);
			readReceipt.setDeviceName(null);
			readReceipt.setType("READ");
			readReceipt.setTimestamp(Instant.now());
			messagingTemplate.convertAndSendToUser(sender, "/queue/receipts", readReceipt);
		}
	}
}