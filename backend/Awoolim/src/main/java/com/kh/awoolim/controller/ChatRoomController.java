package com.kh.awoolim.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.awoolim.domain.ChatRoom;
import com.kh.awoolim.service.ChatRoomService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatRoomController {

	@Autowired
	private ChatRoomService chatRoomService;

	@GetMapping("/api/chatrooms")
	public List<ChatRoom> getChatRooms(HttpServletResponse response) {
		log.info("chatRooms");
		response.setStatus(HttpStatus.OK.value());
		return chatRoomService.getAllChatRooms();
	}
}