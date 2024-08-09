package com.kh.awoolim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.awoolim.domain.Member;
import com.kh.awoolim.mapper.MemberMapper;

@Service
public class MemberService {

	@Autowired
	private MemberMapper mapper;
	@Autowired
	public BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	public Member readMember(int userId) {
		return mapper.findById(userId);
	}
	
	public boolean checkEmail(String userEmail) {
		Member member = null;
		member = mapper.findByEmail(userEmail);
		if(member == null) {
			return false;
		}
		return true;
	}
	
	public boolean checkEmailDefault(String userEmail) {
		Member member = null;
		member = mapper.findByEmail(userEmail);
		if(member != null && member.getSnsType().equals("default")) {
			return true;
		}else {
			return false;
		}
	}
	
	public void register(Member member) {
		member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
		member.setUserImage("dce899f2-eca3-4886-8400-f31bfd64de1f.png");
		member.setUserBackImage("305d04e5-e53d-4419-8beb-555330a6a3d4.png");
		System.out.println(member.getPassword());
		mapper.create(member);
	}
	
	//추가된 부분
	public Member findByPhone(String phone) {
		return mapper.findByPhone(phone);
	}
	
	public void updatePassword(String email,String password) {
		mapper.updatePassword(email, bCryptPasswordEncoder.encode(password));
	}
	
	public Member getProfile(int userId) throws IOException {
		Member member =mapper.profile(userId);
		String userImage = (String)encodeImageToBase64("/static/images/"+member.getUserImage());
		if(member.getUserBackImage() != null && member.getUserBackImage() != "") {
			String backImage = (String)encodeImageToBase64("/static/images/"+member.getUserBackImage());
			member.setUserBackImage(backImage);
		}
		member.setUserImage(userImage);
				
		return member;
	}
	
	public void updateProfile(Member member) {
		mapper.updateProfile(member);
	}
	
	public void updateUser(Member member) {
		mapper.updateUser(member);
	}
	
	public String encodeImageToBase64(String imagePath) throws IOException {
		ClassPathResource imgFile = new ClassPathResource(imagePath);
		byte[] imageBytes = Files.readAllBytes(imgFile.getFile().toPath());
		return Base64.getEncoder().encodeToString(imageBytes);
	}

}
