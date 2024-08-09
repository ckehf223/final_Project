package com.kh.awoolim.common.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.awoolim.common.domain.CustomUserDetails;
import com.kh.awoolim.domain.Member;
import com.kh.awoolim.mapper.AdminMapper;
import com.kh.awoolim.mapper.MemberMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberMapper memberMapper;
	private final AdminMapper adminMapper;

	public CustomUserDetailsService(MemberMapper memberMapper,AdminMapper adminMapper) {
		this.memberMapper = memberMapper;
		this.adminMapper = adminMapper;
	}

	// 데이터 베이스에서 특정유저를 조회해서 return 해주는 메소드
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// 관리자로그인 경우 로직 작성해야함
		Member memberData = null;
		memberData = memberMapper.findByEmail(username);
		log.info("3번 지점");
		if(memberData == null) {
			memberData = adminMapper.findById(username);
		}

		System.out.println(memberData.toString());
		if (memberData != null) {
			return new CustomUserDetails(memberData);
		}

		throw new UsernameNotFoundException("User not found with username: " + username);
	}

}
