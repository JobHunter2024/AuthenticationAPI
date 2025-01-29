package com.jobhunter24.AuthenticationAPI.api.security;

import com.jobhunter24.AuthenticationAPI.api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
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
        // Decode the Base64-encoded secret key
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));

        // Construct the JWT
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationTime))
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

