package com.akshay.privy_backend.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akshay.privy_backend.entity.Chat;
import com.akshay.privy_backend.entity.ChatReadState;
import com.akshay.privy_backend.entity.User;
import com.akshay.privy_backend.repository.ChatReadStateRepository;
import com.akshay.privy_backend.repository.ChatRepository;

@Service
public class ChatReadStateServiceImpl implements ChatReadStateService {

	@Autowired
	private ChatReadStateRepository chatReadStateRepository;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ChatRepository chatRepository;
	
	@Override
	public String updateReadState(UUID chatId, String username, String newMessageId) {
		
		if (chatId == null) {
			throw new IllegalArgumentException("Chat ID cannot be null");
		}
		else if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Username cannot be empty");
		}
		else if (newMessageId != null && !newMessageId.matches("^[0-9A-HJKMNP-TV-Z]{26}$")) {
			throw new IllegalArgumentException("beforeUlid is not valid ULID string");			
		}
		
	    User user = authService.findByUsername(username);

        Optional<ChatReadState> readStateOptional = chatReadStateRepository.findByChatIdAndUserId(chatId, user.getId());

        ChatReadState state;
        if (readStateOptional.isPresent()) {
            state = readStateOptional.get();

            if (newMessageId.compareTo(state.getLastReadMessageId()) > 0) {
                state.setLastReadMessageId(newMessageId);
                state.setUpdatedAt(Instant.now());
                chatReadStateRepository.save(state);
            }
        } else {
            state = new ChatReadState();
            Chat chat = chatRepository.findById(chatId)
    				.orElseThrow(() -> new IllegalArgumentException("Chat not found"));
            state.setChat(chat);
            state.setUser(user);
            state.setLastReadMessageId(newMessageId);
            state.setUpdatedAt(Instant.now());
            chatReadStateRepository.save(state);
        }
		return state.getLastReadMessageId();
	}

}
