package com.mealrescue.security;

import com.mealrescue.model.AccountStatus;
import com.mealrescue.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long userId = jwtService.extractUserId(token);
                userRepository.findById(userId)
                        .filter(user -> user.getStatus() == AccountStatus.ACTIVE)
                        .ifPresent(user -> {
                            var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
                            var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (JwtException | IllegalArgumentException e) {
                // Bad or expired token: stay "not logged in"; the security rules will answer 401
            }
        }

        filterChain.doFilter(request, response);
    }
}
