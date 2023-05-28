package com.polzzak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
	@GetMapping
	public String test(HttpServletRequest request) {
		String requestIp = request.getHeader("X-Forwarded-For");
		String remoteAddr = request.getRemoteAddr();
		log.error("requestIp = {}", requestIp);
		log.error("remoteAddr = {}", remoteAddr);
		return "test!!";
	}
}
