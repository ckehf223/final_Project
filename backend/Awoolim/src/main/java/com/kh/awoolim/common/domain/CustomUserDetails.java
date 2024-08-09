package com.kh.awoolim.common.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.kh.awoolim.common.auth.Oauth2Response;
import com.kh.awoolim.domain.Member;

public class CustomUserDetails implements UserDetails, OAuth2User {
	private final Member member;
	private final Oauth2Response oauth2Response;

	public CustomUserDetails(Member member) {
		this.member = member;
		this.oauth2Response = null;
	}

	public CustomUserDetails(Member member, Oauth2Response oauth2Response) {
		this.member = member;
		this.oauth2Response = oauth2Response;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return member.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getUserName();
	}
	
	public String getUserEmail() {
		return member.getUserEmail();
	}

	public int getUserId() {
		return member.getUserId();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}