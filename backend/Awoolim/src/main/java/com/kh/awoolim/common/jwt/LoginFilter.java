package com.kh.awoolim.common.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import com.kh.awoolim.common.domain.CustomUserDetails;
import com.kh.awoolim.common.domain.RefreshToken;
import com.kh.awoolim.mapper.RefreshTokenMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final RefreshTokenMapper refreshTokenMapper;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
			RefreshTokenMapper refreshTokenMapper) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshTokenMapper = refreshTokenMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String requestBody = sb.toString();
			JSONObject json = new JSONObject(requestBody);
			String username = json.getString("username");
			String password = json.getString("password");
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);
			return authenticationManager.authenticate(authToken);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse authentication request body", e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		log.info("successfulAuthentication");
		CustomUserDetails member = (CustomUserDetails) authentication.getPrincipal();
		String userEmail = member.getUserEmail();
		int userId = member.getUserId();
		
		Collection<? extends GrantedAuthority> authorities = member.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();
		deleteRefreshToken(userId);

		
		String access = jwtUtil.createJwt("access", userEmail, role, 3600000L,userId);
		String refresh = jwtUtil.createJwt("refresh", userEmail, role, 86400000L,userId);
		addRefreshToken(userEmail, refresh, 86400000L,userId);
		response.setHeader("Authorization", "Bearer " + access);
		response.addCookie(createCookie("refresh", refresh));
		response.setStatus(HttpStatus.OK.value());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		log.info("unsuccess");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	@Transactional
	private void addRefreshToken(String username, String refresh, Long expiredMs,int userId) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUserEmail(username);
		refreshToken.setRefresh(refresh);
		refreshToken.setUserId(userId);
		refreshToken.setExpiration(new Date(System.currentTimeMillis() + expiredMs).toString());
		refreshTokenMapper.create(refreshToken);
	}

	@Transactional
	private void deleteRefreshToken(int userId) {
		refreshTokenMapper.deleteAll(userId);
	}
}
