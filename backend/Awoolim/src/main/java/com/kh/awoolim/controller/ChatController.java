package com.kh.awoolim.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.kh.awoolim.domain.Chat;
import com.kh.awoolim.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

//	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	private ChatService chatService;

//	@Autowired
//	private SimpMessagingTemplate messagingTemplate;

	// 클라이언트가 특정 클럽 번호의 메시지를 가져올 때 사용하는 엔드포인트
	@GetMapping("/{clubNo}/messages")
	public List<Chat> getMessages(@PathVariable int clubNo) {
		try {
			return chatService.getMessagesByClub(clubNo);
		} catch (Exception e) {
//			logger.error("Error fetching messages for clubNo {}: {}", clubNo, e.getMessage());
			return List.of(); // 오류가 발생하면 빈 리스트를 반환
		}
	}

	// WebSocket을 통해 메시지를 전송하는 엔드포인트
	@MessageMapping("/sendMessage")
	@SendTo("/topic/public")
	public Chat sendMessageWebSocket(Chat chatMessage) {
		try {
			chatService.saveMessage(chatMessage.getClubNo(), chatMessage);
//			logger.info("Message saved and broadcasted: {}", chatMessage);
		} catch (Exception e) {
//			logger.error("Error saving message: {}", chatMessage, e);
//			messagingTemplate.convertAndSend("/topic/errors", "Error processing message: " + e.getMessage());
		}
		return chatMessage;
	}

	// WebSocket을 통해 새로운 사용자를 추가하는 엔드포인트
	@MessageMapping("/addUser")
	@SendTo("/topic/public")
	public Chat addUserWebSocket(Chat chatMessage) {
		try {
			chatMessage.setMessage(chatMessage.getUserId() + " joined!");
//			logger.info("User joined: {}", chatMessage.getUserId());
		} catch (Exception e) {
//			logger.error("Error adding user: {}", chatMessage.getUserId(), e);
//			messagingTemplate.convertAndSend("/topic/errors", "Error adding user: " + e.getMessage());
		}
		return chatMessage;
	}
}
