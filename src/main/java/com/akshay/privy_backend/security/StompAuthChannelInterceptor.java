package com.akshay.privy_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            accessor.setLeaveMutable(true);
            
            Object authObj = accessor.getSessionAttributes().get("SPRING.AUTHENTICATION");
            if (authObj instanceof Authentication auth) {
                accessor.setUser(auth);
                return message;
            }

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                authHeader = accessor.getFirstNativeHeader("authorization");
            }

            if (!authHeader.startsWith("Bearer ")) {
                throw new MessagingException("Invalid Authorization header for STOMP CONNECT");
            }
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String username = jwtService.extractUsername(token);

                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                        );

                    accessor.setUser(auth);

                } catch (Exception ex) {
                    throw new MessagingException("Invalid token for STOMP CONNECT: " + ex.getMessage());
                }
            }
        }

        return message;
    }
}