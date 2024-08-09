package com.kh.awoolim.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.Duration;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

//	private static final Logger logger = LoggerFactory.getLogger(CorsMvcConfig.class);

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
//		LocalDateTime startTime = LocalDateTime.now();
//		logger.info("CORS configuration started at: {}", startTime);

		corsRegistry.addMapping("/**").allowedOrigins("http://localhost:5173").allowedMethods("*").allowedHeaders("*")
				.exposedHeaders("Authorization", "LoginId")
				.allowCredentials(true).maxAge(6000L);

//		LocalDateTime endTime = LocalDateTime.now();
//		logger.info("CORS configuration completed at: {}", endTime);
//		logger.info("CORS configuration took: {} ms", Duration.between(startTime, endTime).toMillis());
	}
}
