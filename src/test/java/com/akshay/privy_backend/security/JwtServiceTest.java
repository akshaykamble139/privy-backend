package com.akshay.privy_backend.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "app.jwt.secret=mySecretKey1234567890123456789012345678",
    "app.jwt.expiration=3600000"
})
public class JwtServiceTest {
	
	@Autowired
    private JwtService jwtService; 
	        	
	@Test
	public void generateTokenTest() {

		String username = "testUser";
	    String token = jwtService.generateToken(username);
                
        String extractedUsername = jwtService.extractUsername(token);
        Assertions.assertEquals(username, extractedUsername, "Username in token should match the input username");
	}
	
	@Test
	public void validateTokenTest() {

		String username = "testUser";
	    String token = jwtService.generateToken(username);
                
        boolean result = jwtService.validateToken(token,username);
        Assertions.assertEquals(true, result, "Username in token should match the input username");

        result = jwtService.validateToken(token,"test");
        Assertions.assertNotEquals(true, result, "Username in token not should match the input username");
	}
}
