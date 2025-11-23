package com.akshay.privy_backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akshay.privy_backend.entity.Chat;
import com.akshay.privy_backend.entity.ChatReadState;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.ChatReadStateRepository;
import com.akshay.privy_backend.repository.ChatRepository;

@ExtendWith(MockitoExtension.class)
public class ChatReadStateServiceImplTest {

	@Mock
	private ChatReadStateRepository chatReadStateRepository;

	@Mock
	private AuthService authService;

	@Mock
	private ChatRepository chatRepository;

	@InjectMocks
	private ChatReadStateServiceImpl chatReadStateService;

	@Test
	public void updateReadStateTest() {
		boolean result = true;

		try {
			chatReadStateService.updateReadState(null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);

		UUID chatId = UUID.randomUUID();
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, " ", null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String username = "akshay";
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, "fuygiugyiyg");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String messageId = "01HZX5H0Q6HD8WET8K5X3A9KNV";
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, messageId);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		User user = new User();
		user.setId(chatId);
		when(authService.findByUsername(anyString())).thenReturn(user);
		
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, messageId);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		Chat chat = new Chat();
		when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, messageId);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		ChatReadState state = new ChatReadState();
		state.setLastReadMessageId(messageId);
		when(chatReadStateRepository
				.findByChatIdAndUserId(any(UUID.class),any(UUID.class))).thenReturn(Optional.of(state));
		
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, messageId);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		state.setLastReadMessageId("000000");
		when(chatReadStateRepository
				.findByChatIdAndUserId(any(UUID.class),any(UUID.class))).thenReturn(Optional.of(state));
		
		result = true;
		try {
			chatReadStateService.updateReadState(chatId, username, messageId);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
	}
}
