package com.jobhunter24.AuthenticationAPI.api.security;

import com.jobhunter24.AuthenticationAPI.api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(User user) {
        // Create a signing key from the secret
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        // Use Jwts.builder() to construct the JWT
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expirationTime))
                .signWith(key)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateAndParseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Set the secret key
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody(); // Extract the token body (claims)
    }

}

