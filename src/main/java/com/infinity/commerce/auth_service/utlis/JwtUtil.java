package com.infinity.commerce.auth_service.utlis;

import com.infinity.commerce.auth_service.entity.User;
import com.infinity.commerce.auth_service.pojo.UserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component


public class JwtUtil {
    private static final String SECRET_KEY = "YOUR_SECRET_KEY_MUST_BE_32_BYTES_OR_LONGER";
    private static final long EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    //public or private method?
    public String generateToken(UserDetails user) {
        // Fetch roles
        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername()) // Set username
                .claim("roles", roles) // Add roles in JWT claims
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
                //.signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign token
                .signWith(key)
                //should it be key? or secret key
                .compact();
    }

}