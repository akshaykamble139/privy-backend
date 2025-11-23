package com.akshay.privy_backend.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.akshay.privy_backend.entity.MediaFile;
import com.akshay.privy_backend.entity.Message;
import com.akshay.privy_backend.repository.ChatMemberRepository;
import com.akshay.privy_backend.repository.MediaFileRepository;
import com.akshay.privy_backend.repository.MessageRepository;

@Service
public class MediaServiceImpl implements MediaService {

	@Autowired
	private MediaFileRepository mediaRepo;

	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private ChatMemberRepository chatMemberRepository;

	@Override
	public MediaFile storeEncryptedFileForChat(UUID chatId,
            String username,MultipartFile file, String filename, String mimeType) throws IOException, NoSuchAlgorithmException {
		
		if (chatId == null)
			throw new IllegalArgumentException("Chat ID cannot be null");
		else if (username == null || username.isBlank()) 
			throw new IllegalArgumentException("Username cannot be empty");
		else if (filename == null || filename.isBlank()) 
			throw new IllegalArgumentException("File name cannot be empty");
		else if (mimeType == null || mimeType.isBlank()) 
			throw new IllegalArgumentException("mimeType cannot be empty");
		else if (file == null)
			throw new IllegalArgumentException("File cannot be null");
		
		boolean isMember = chatMemberRepository.existsByChatIdAndUserUsername(chatId, username);
        if (!isMember) {
            throw new SecurityException("You are not a member of this chat.");
        }
        
		byte[] ciphertext = file.getBytes();
		String hash = computeSHA256(ciphertext);

		return mediaRepo.findByHash(hash).orElseGet(() -> {
			MediaFile mf = new MediaFile();
			mf.setCiphertext(ciphertext);
			mf.setFilename(filename);
			mf.setMimeType(mimeType);
			mf.setSize(ciphertext.length);
			mf.setHash(hash);
			mf.setCreatedAt(Instant.now());
			return mediaRepo.save(mf);
		});
	}

	private String computeSHA256(byte[] data) throws NoSuchAlgorithmException {
		return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(data));
	}
	
	@Override
    public MediaFile getChatFile(UUID chatId, UUID fileId, String username) {
		
		if (chatId == null)
			throw new IllegalArgumentException("Chat ID cannot be null");
		else if (fileId == null)
			throw new IllegalArgumentException("File ID cannot be null");
		else if (username == null || username.isBlank()) 
			throw new IllegalArgumentException("Username cannot be empty");

		boolean isMember = chatMemberRepository.existsByChatIdAndUserUsername(chatId, username);
        if (!isMember) {
            throw new SecurityException("Not authorized to download files from this chat");
        }

        List<Message> messages = messageRepository.findByChatIdAndMediaFileId(chatId, fileId);
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("File is not associated with the provided chat");
        }
        
        return mediaRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }
	
	@Override
	public void markFileAttached(UUID fileId) {
	    mediaRepo.findById(fileId).ifPresent(f -> {
	        if (!f.isAttachedToMessage()) {
	            f.setAttachedToMessage(true);
	            mediaRepo.save(f);
	        }
	    });
	}
}
