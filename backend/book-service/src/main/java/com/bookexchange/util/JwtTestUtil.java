package com.bookexchange.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTestUtil {

    private static final String SECRET_KEY = "D7vK1gE9ZqP4nW0sT3bLrY6xM2aQ8cUfH5eJiXpCtNhRvSzLmYgBqWdXeTkV9uJo";

    public static String generateToken(Long userId, String email, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }
}
