package com.example.mybank.infrastructure.driving.rest.security;

import com.example.mybank.domain.model.Client;
import com.example.mybank.infrastructure.driven.jpa.JpaUserSpringRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final JpaUserSpringRepository userRepo;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, JpaUserSpringRepository userRepo) {
        this.jwtTokenService = jwtTokenService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        String queryAuth = request.getParameter("authorization");
        if ((auth != null && auth.startsWith("Bearer ")) || queryAuth != null) {
            String token = auth != null ? auth.substring(7) : queryAuth;
            try {
                Map<String, Object> claims = jwtTokenService.parseClaims(token);
                String login = (String) claims.getOrDefault("sub", claims.get("login"));
                String clientIdStr = (String) claims.get("clientId");
                if (login != null && clientIdStr != null) {
                    var opt = userRepo.findByLoginIgnoreCase(login);
                    if (opt.isPresent()) {
                        var principal = new MyBankPrincipal(login, Client.Id.from(clientIdStr));
                        var authToken = new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        filterChain.doFilter(request, response);
    }

    public record MyBankPrincipal(String login, Client.Id clientId) {
    }
}
