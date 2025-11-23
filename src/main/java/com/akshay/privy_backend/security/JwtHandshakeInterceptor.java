package com.akshay.privy_backend.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor{

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		if (request instanceof ServletServerHttpRequest servletReq) {
			HttpServletRequest req = servletReq.getServletRequest();
			String authHeader = req.getHeader("Authorization");

	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        	String token = authHeader.substring(7);

		        String username = jwtService.extractUsername(token);
		        UserDetails user = userDetailsService.loadUserByUsername(username);
                attributes.put("SPRING.AUTHENTICATION", new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

	        }
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}

}
