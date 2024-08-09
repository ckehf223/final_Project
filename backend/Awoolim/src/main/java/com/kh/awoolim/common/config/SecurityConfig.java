package com.kh.awoolim.common.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.kh.awoolim.common.auth.CustomAuthenticationEntryPoint;
import com.kh.awoolim.common.auth.Oauth2LoginFailureHandler;
import com.kh.awoolim.common.auth.Oauth2LoginSuccessHandler;
import com.kh.awoolim.common.auth.Oauth2UserDetailsService;
import com.kh.awoolim.common.jwt.CustomLogoutFilter;
import com.kh.awoolim.common.jwt.JWTFilter;
import com.kh.awoolim.common.jwt.JWTUtil;
import com.kh.awoolim.common.jwt.LoginFilter;
import com.kh.awoolim.mapper.RefreshTokenMapper;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final RefreshTokenMapper refreshTokenMapper;
	private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;
	private final Oauth2UserDetailsService oauth2UserDetailsService;
	private final Oauth2LoginFailureHandler oauth2LoginFailureHandler;
	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
			RefreshTokenMapper refreshTokenMapper, Oauth2LoginSuccessHandler oauth2LoginSuccessHandler,
			Oauth2UserDetailsService oauth2UserDetailsService, Oauth2LoginFailureHandler oauth2LoginFailureHandler) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.refreshTokenMapper = refreshTokenMapper;
		this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
		this.oauth2UserDetailsService = oauth2UserDetailsService;
		this.oauth2LoginFailureHandler = oauth2LoginFailureHandler;
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration configuration = new CorsConfiguration();
				configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(6000L);
				configuration.setExposedHeaders(Arrays.asList("Authorization","LoginId"));
				return configuration;
			}
		}));


		http.csrf(auth -> auth.disable());
		http.formLogin(auth -> auth.disable());
		http.httpBasic(auth -> auth.disable());

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/").permitAll()
				.requestMatchers("/auth/*").permitAll()
				.requestMatchers("/refresh").permitAll()
				.requestMatchers("/auth/kakao/**").permitAll()
				.requestMatchers("/send-sms").permitAll()
				.requestMatchers("/check-code").permitAll()
				.requestMatchers("/auth/naver/**").permitAll()
				.requestMatchers("/auth/google/**").permitAll()
				.requestMatchers("/club/register").hasRole("MEMBER")
				.requestMatchers("/admin/").permitAll()
//				.requestMatchers("/admin/*").hasRole("ADMIN")
				.anyRequest().permitAll());

		http.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint(
						(userInfoEndpointConfig) -> userInfoEndpointConfig.userService(oauth2UserDetailsService))
				.successHandler(oauth2LoginSuccessHandler).failureHandler(oauth2LoginFailureHandler));

		http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
		http.addFilterAt(
				new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenMapper),
				UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenMapper), LogoutFilter.class);
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
