package com.kh.awoolim.common.auth;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.awoolim.common.domain.RefreshToken;
import com.kh.awoolim.common.jwt.JWTUtil;
import com.kh.awoolim.mapper.RefreshTokenMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
public class ReissueController {

	private final JWTUtil jwtUtil;
	private RefreshTokenMapper refreshTokenMapper;

	public ReissueController(JWTUtil jwtUtil, RefreshTokenMapper refreshTokenMapper) {

		this.jwtUtil = jwtUtil;
		this.refreshTokenMapper = refreshTokenMapper;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		// get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refresh")) {

				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			// response status code
			return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
		}

		// expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			// response status code
			return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {

			// response status code
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		// DB에 저장되어 있는지 확인
		RefreshToken isExist = refreshTokenMapper.read(refresh);
		if (isExist == null) {
			// response body
			return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);

		// make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

		// Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		deleteRefreshToken(refresh);
		addRefreshToken(username, newRefresh, 86400000L);

		// response
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		// cookie.setSecure(true);
		// cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

	@Transactional
	private void deleteRefreshToken(String refresh) {
		refreshTokenMapper.delete(refresh);
	}

	@Transactional
	private void addRefreshToken(String username, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUserName(username);
		refreshToken.setRefresh(refresh);
		refreshToken.setExpiration(date.toString());

		refreshTokenMapper.create(refreshToken);
	}

}