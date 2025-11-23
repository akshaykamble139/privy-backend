package com.akshay.privy_backend.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.akshay.privy_backend.entity.MediaFile;

public interface MediaService {
    MediaFile getChatFile(UUID chatId, UUID fileId, String username);
	MediaFile storeEncryptedFileForChat(UUID chatId, String name, MultipartFile file, String originalFilename,
			String contentType) throws IOException, NoSuchAlgorithmException;
	void markFileAttached(UUID fileId);
}
