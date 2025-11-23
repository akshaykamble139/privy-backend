package com.akshay.privy_backend.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.akshay.privy_backend.config.SecurityConfig;
import com.akshay.privy_backend.entity.MediaFile;
import com.akshay.privy_backend.security.JwtAuthenticationFilter;
import com.akshay.privy_backend.service.MediaService;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MediaController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class MediaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MediaService mediaService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	public void uploadTest() {

		UUID chatId = UUID.randomUUID();
		String fileName = "testFile.png";
		String contentType = "image/png";
		long fileSize = 12345L;

		Principal principal = () -> "testuser";

		MediaFile mockMediaFile = new MediaFile();
		mockMediaFile.setId(UUID.randomUUID());
		mockMediaFile.setSize(fileSize);

		try {
			when(mediaService.storeEncryptedFileForChat(any(UUID.class), anyString(), any(MultipartFile.class),
					anyString(), anyString())).thenReturn(mockMediaFile);

			MockMultipartFile file = new MockMultipartFile("file", fileName, contentType, "file content".getBytes());

			mockMvc.perform(multipart("/api/media/upload/{chatId}", chatId)
						.file(file)
						.principal(principal))
					.andExpect(status().isOk())
	                .andExpect(jsonPath("$.fileId").value(mockMediaFile.getId().toString()))
	                .andExpect(jsonPath("$.dedup").value(false));
			
	        byte[] content = "This is a test file content.".getBytes();
	        file = new MockMultipartFile("file", fileName, contentType, content);
	        
			mockMediaFile.setSize(content.length);

	        
	        when(mediaService.storeEncryptedFileForChat(any(UUID.class), anyString(), any(MultipartFile.class),
	        		anyString(), anyString())).thenReturn(mockMediaFile);

			mockMvc.perform(multipart("/api/media/upload/{chatId}", chatId)
						.file(file)
						.principal(principal))
					.andExpect(status().isOk())
	                .andExpect(jsonPath("$.fileId").value(mockMediaFile.getId().toString()))
	                .andExpect(jsonPath("$.dedup").value(true));
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void downloadFromChatTest() {
		
		UUID chatId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        String fileName = "testFile.png";
        String mimeType = "image/png";
        byte[] fileContent = "file content".getBytes();  

        Principal principal = () -> "testuser";

        MediaFile mockMediaFile = new MediaFile();
        mockMediaFile.setFilename(fileName);
        mockMediaFile.setMimeType(mimeType);
        mockMediaFile.setCiphertext(fileContent);

        when(mediaService.getChatFile(any(UUID.class), any(UUID.class), anyString())).thenReturn(mockMediaFile);

        try {
			mockMvc.perform(get("/api/media/chats/{chatId}/files/{fileId}", chatId, fileId)
			                .principal(principal))
			        .andExpect(status().isOk())
			        .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\""))
			        .andExpect(content().contentType(mimeType))
			        .andExpect(content().bytes(fileContent));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
