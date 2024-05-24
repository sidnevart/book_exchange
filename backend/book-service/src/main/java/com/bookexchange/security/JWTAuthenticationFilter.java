package com.bookexchange.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final String jwtSecret = "D7vK1gE9ZqP4nW0sT3bLrY6xM2aQ8cUfH5eJiXpCtNhRvSzLmYgBqWdXeTkV9uJo"; // или через @Value

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);

            var auth = new UsernamePasswordAuthenticationToken(
                    new CurrentUser(userId, email, role),
                    null,
                    List.of(() -> "ROLE_" + role)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
        }

        filterChain.doFilter(request, response);
    }

}
