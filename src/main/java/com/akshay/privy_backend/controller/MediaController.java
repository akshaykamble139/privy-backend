package com.akshay.privy_backend.controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.akshay.privy_backend.entity.MediaFile;
import com.akshay.privy_backend.service.MediaService;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping("/upload/{chatId}")
    public ResponseEntity<Map<String, Object>> upload(
    		@PathVariable UUID chatId,
            @RequestParam("file") MultipartFile file,
            Principal principal) throws Exception  {

        MediaFile stored = mediaService.storeEncryptedFileForChat(
        		chatId,
        		principal.getName(),
                file,
                file.getOriginalFilename(),
                file.getContentType()
        );

        return ResponseEntity.ok(
        		Map.of(
                "fileId", stored.getId(),
                "dedup", (stored.getSize() == file.getSize())
        ));
    }
    
    @GetMapping("/chats/{chatId}/files/{fileId}")
    public ResponseEntity<byte[]> downloadFromChat(
            @PathVariable UUID chatId,
            @PathVariable UUID fileId,
            Principal principal) {

        MediaFile mf = mediaService.getChatFile(chatId, fileId, principal.getName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mf.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(mf.getMimeType() == null ? "application/octet-stream" : mf.getMimeType()))
                .body(mf.getCiphertext());
    }

}
