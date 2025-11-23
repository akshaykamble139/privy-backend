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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.akshay.privy_backend.entity.MediaFile;
import com.akshay.privy_backend.entity.Message;
import com.akshay.privy_backend.repository.ChatMemberRepository;
import com.akshay.privy_backend.repository.MediaFileRepository;
import com.akshay.privy_backend.repository.MessageRepository;
import com.github.f4b6a3.ulid.UlidCreator;

@ExtendWith(MockitoExtension.class)
public class MediaServiceImplTest {
	
	@Mock
	private MediaFileRepository mediaRepo;

	@Mock
	private MessageRepository messageRepository;
	
	@Mock
	private ChatMemberRepository chatMemberRepository;

	@InjectMocks
	private MediaServiceImpl mediaService;
	
	@Test
	public void storeEncryptedFileForChatTest() {
		boolean result = true;
		try {
			mediaService.storeEncryptedFileForChat(null, null, null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		UUID chatId = UUID.randomUUID();
		
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, null, null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, " ", null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String username = "akshay";
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , null, " ", null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
        String fileName = "test-file.txt";
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , null, fileName, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , null, fileName, " ");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
        String contentType = "text/plain";
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , null, fileName, contentType);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String fieldName = "file";
        byte[] content = "This is a test file content.".getBytes();

        MultipartFile file = new MockMultipartFile(fieldName, fileName, contentType, content);
        
        result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , file, fileName, contentType);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
        
		when(chatMemberRepository.existsByChatIdAndUserUsername(any(UUID.class),anyString())).thenReturn(true);
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , file, fileName, contentType);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
		
		MediaFile mf = new MediaFile();
		mf.setFilename(fileName);
		mf.setMimeType(contentType);
		mf.setSize(content.length);
		mf.setCreatedAt(Instant.now());
		
		when(mediaRepo.findByHash(anyString())).thenReturn(Optional.of(mf));
		result = true;
		try {
			mediaService.storeEncryptedFileForChat(chatId, username , file, fileName, contentType);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);
	}
	
	@Test
	public void getChatFileTest() {
		boolean result = true;
		try {
			mediaService.getChatFile(null, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		UUID chatId = UUID.randomUUID();
		
		result = true;
		try {
			mediaService.getChatFile(chatId, null, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		UUID fileId = UUID.randomUUID();

		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, null);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, " ");
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		String username = "akshay";
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		when(chatMemberRepository.existsByChatIdAndUserUsername(any(UUID.class),anyString())).thenReturn(true);
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
				
		when(messageRepository.findByChatIdAndMediaFileId(any(UUID.class),any(UUID.class))).thenReturn(null);
		
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		List<Message> messages = new ArrayList<>();
				
		when(messageRepository.findByChatIdAndMediaFileId(any(UUID.class),any(UUID.class))).thenReturn(messages);
		
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		Message message = new Message();
		message.setId(UlidCreator.getUlid().toString());
		messages.add(message);
		when(messageRepository.findByChatIdAndMediaFileId(any(UUID.class),any(UUID.class))).thenReturn(messages);

		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(false, result);
		
		MediaFile mediaFile = new MediaFile();
		mediaFile.setFilename("text.txt");
		
		when(mediaRepo.findById(any(UUID.class))).thenReturn(Optional.of(mediaFile));
		result = true;
		try {
			mediaService.getChatFile(chatId, fileId, username);
		} catch (Exception e) {
			result = false;
		}
		Assertions.assertEquals(true, result);		
	}
	
	@Test
	public void markFileAttachedTest() {
		boolean result = true;
		try {
			mediaService.markFileAttached(UUID.randomUUID());
		} catch (Exception e) {
			result = false;
		}
		
		Assertions.assertEquals(true, result);
		
		MediaFile mediaFile = new MediaFile();
		when(mediaRepo.findById(any(UUID.class))).thenReturn(Optional.of(mediaFile));
		
		try {
			mediaService.markFileAttached(UUID.randomUUID());
		} catch (Exception e) {
			result = false;
		}
		
		Assertions.assertEquals(true, result);
		mediaFile.setAttachedToMessage(true);
		
		when(mediaRepo.findById(any(UUID.class))).thenReturn(Optional.of(mediaFile));
		
		try {
			mediaService.markFileAttached(UUID.randomUUID());
		} catch (Exception e) {
			result = false;
		}
		
		Assertions.assertEquals(true, result);
		
	}
}
