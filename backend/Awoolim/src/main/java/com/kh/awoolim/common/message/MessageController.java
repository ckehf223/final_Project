package com.kh.awoolim.common.message;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.awoolim.domain.Member;
import com.kh.awoolim.service.MemberService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Slf4j
@RestController
public class MessageController {

	final DefaultMessageService messageService;

	@Autowired
	private MemberService memberService;

	@Value("${coolsms.api.sendNumber}")
	private String sendNumber;

	public MessageController(@Value("${coolsms.api.key}") String apiKey,
			@Value("${coolsms.api.secret}") String apiSecret) {
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
	}

	@PostMapping("send-sms")
	public void sendSms(@RequestBody MessageRequest messageRequest, HttpSession session, HttpServletResponse response) {
		String code = randomCode();
		session.setAttribute("authenticationCode", code);

		Message message = new Message();
		message.setFrom(sendNumber);
		message.setTo(messageRequest.getPhoneNumber());
		message.setText("[어울림 인증서비스]\n인증번호 ( " + code + " )를 입력해주세요");

		try {
			messageService.send(message);
			log.info("message" + message);
			response.setStatus(HttpStatus.OK.value());
		} catch (NurigoMessageNotReceivedException exception) {
			throw new RuntimeException("message error " + exception.getMessage());
		} catch (Exception exception) {
			throw new RuntimeException("message error " + exception.getMessage());
		}
	}

	@PostMapping("/check-code")
	public ResponseEntity<Integer> checkCode(@RequestBody CodeRequest codeRequest, HttpSession session) {
		String storedCode = (String) session.getAttribute("authenticationCode");
		log.info("Received Code: " + codeRequest.getCode());
		log.info("Stored Code: " + storedCode);
		log.info("Phone Number: " + codeRequest.getPhoneNumber());

		if (storedCode == null) {
			log.info("Stored code is null");
			return ResponseEntity.status(HttpStatus.OK).body(Integer.valueOf(-1));
		}

		if (storedCode.equals(codeRequest.getCode())) {
			try {
				Member member = memberService.findByPhone(codeRequest.getPhoneNumber());
				
				if (member == null) {
					log.info("check=code ENter");
					return ResponseEntity.status(HttpStatus.OK).body(Integer.valueOf(1));
				} else {
					return ResponseEntity.status(HttpStatus.OK).body(Integer.valueOf(0));
				}
			} catch (Exception e) {
				log.error("Error occurred while checking code", e);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Integer.valueOf(-1));
			}
		} else {
			log.info("Codes do not match");
			return ResponseEntity.status(HttpStatus.OK).body(Integer.valueOf(-1));
		}
	}

	private String randomCode() {
		SecureRandom random = new SecureRandom();
		int number = 100000 + random.nextInt(900000);
		return String.valueOf(number);
	}
}
