package com.agrocrm.security;

import com.agrocrm.domain.session.SessionService;
import com.agrocrm.domain.session.UserSession;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final SessionService sessionService;

    public JwtAuthFilter(JwtService jwtService, SessionService sessionService) {
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                // Проверяем, что это access токен
                if (!jwtService.isAccessToken(token)) {
                    log.debug("Token is not an access token");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Проверяем JWT токен
                Claims claims = jwtService.parse(token);
                String username = claims.getSubject();
                
                // Поддержка как старого формата (одна роль), так и нового (множественные роли)
                List<String> roles;
                if (claims.get("roles") != null) {
                    // Новый формат: массив ролей
                    @SuppressWarnings("unchecked")
                    List<String> rolesList = claims.get("roles", List.class);
                    roles = rolesList;
                } else if (claims.get("role") != null) {
                    // Старый формат: одна роль
                    String role = claims.get("role", String.class);
                    roles = List.of(role);
                } else {
                    log.debug("No roles found in JWT token");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                if (username != null && !roles.isEmpty()) {
                    // Получаем ID сессии из JWT
                    String sessionId = claims.get("sessionId", String.class);
                    if (sessionId != null) {
                        // Проверяем сессию в базе данных
                        UserSession session = sessionService.validateSessionById(sessionId);
                        if (session != null) {
                            // Создаем список авторитетов из ролей
                            List<SimpleGrantedAuthority> authorities = roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .toList();
                            
                            var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            log.debug("Session not found or expired for sessionId: {}", sessionId);
                        }
                    } else {
                        log.debug("No sessionId found in JWT token");
                    }
                }
            } catch (Exception e) {
                log.debug("JWT token validation failed: {}", e.getMessage());
                // ignore, unauthenticated request will be handled downstream
            }
        }
        filterChain.doFilter(request, response);
    }
}
