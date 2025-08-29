package com.agrocrm.domain.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);
    
    private final UserSessionRepository sessionRepository;

    public SessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public UserSession createSession(UUID userId, String sessionToken, String userAgent, String ipAddress) {
        try {
            // Определяем тип устройства на основе User-Agent
            Map<String, Object> deviceInfo = parseDeviceInfo(userAgent);
            
            // Устанавливаем время истечения (12 часов)
            OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(12);
            
            UserSession session = new UserSession(userId, sessionToken, deviceInfo, ipAddress, userAgent, expiresAt);
            UUID sessionId = sessionRepository.createSession(session);
            session.setId(sessionId);
            
            // Логируем создание сессии
            sessionRepository.logSessionAudit(userId, sessionId, "SESSION_CREATED", deviceInfo, ipAddress, userAgent);
            
            log.info("Created session: userId={}, sessionId={}, deviceType={}", userId, sessionId, deviceInfo.get("type"));
            return session;
        } catch (Exception e) {
            log.error("Failed to create session: userId={}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void updateSessionToken(UUID sessionId, String sessionToken) {
        try {
            sessionRepository.updateSessionToken(sessionId, sessionToken);
            log.debug("Updated session token for sessionId: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to update session token: sessionId={}", sessionId, e);
            throw e;
        }
    }

    public UserSession validateSession(String sessionToken) {
        try {
            UserSession session = sessionRepository.findByToken(sessionToken);
            if (session != null) {
                // Обновляем время последней активности
                sessionRepository.updateLastActivity(session.getId());
                return session;
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to validate session: token={}", sessionToken, e);
            return null;
        }
    }

    public UserSession validateSessionById(String sessionId) {
        try {
            UUID sessionUuid = UUID.fromString(sessionId);
            UserSession session = sessionRepository.findById(sessionUuid);
            if (session != null && session.getIsActive() && session.getExpiresAt().isAfter(OffsetDateTime.now())) {
                // Обновляем время последней активности
                sessionRepository.updateLastActivity(session.getId());
                return session;
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to validate session by ID: sessionId={}", sessionId, e);
            return null;
        }
    }

    public List<UserSession> getUserSessions(UUID userId) {
        try {
            return sessionRepository.findByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get user sessions: userId={}", userId, e);
            throw e;
        }
    }

    public List<UserSession> getActiveUserSessions(UUID userId) {
        try {
            return sessionRepository.findActiveByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get active user sessions: userId={}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void terminateSession(UUID sessionId, UUID userId, String ipAddress, String userAgent) {
        try {
            UserSession session = sessionRepository.findById(sessionId);
            if (session != null && session.getUserId().equals(userId)) {
                sessionRepository.deactivateSession(session.getId());
                sessionRepository.logSessionAudit(userId, session.getId(), "SESSION_TERMINATED", 
                                                session.getDeviceInfo(), ipAddress, userAgent);
                log.info("Terminated session: sessionId={}, userId={}", sessionId, userId);
            }
        } catch (Exception e) {
            log.error("Failed to terminate session: sessionId={}, userId={}", sessionId, userId, e);
            throw e;
        }
    }

    @Transactional
    public void terminateAllOtherSessions(UUID currentSessionId, UUID userId, String ipAddress, String userAgent) {
        try {
            List<UserSession> activeSessions = sessionRepository.findActiveByUserId(userId);
            for (UserSession session : activeSessions) {
                if (!session.getId().equals(currentSessionId)) {
                    sessionRepository.deactivateSession(session.getId());
                    sessionRepository.logSessionAudit(userId, session.getId(), "SESSION_TERMINATED", 
                                                    session.getDeviceInfo(), ipAddress, userAgent);
                }
            }
            log.info("Terminated all other sessions for user: userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to terminate all other sessions: userId={}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void logout(UUID sessionId, UUID userId, String ipAddress, String userAgent) {
        try {
            UserSession session = sessionRepository.findById(sessionId);
            if (session != null && session.getUserId().equals(userId)) {
                sessionRepository.deactivateSession(session.getId());
                sessionRepository.logSessionAudit(userId, session.getId(), "LOGOUT", 
                                                session.getDeviceInfo(), ipAddress, userAgent);
                log.info("User logged out: sessionId={}, userId={}", sessionId, userId);
            }
        } catch (Exception e) {
            log.error("Failed to logout: sessionId={}, userId={}", sessionId, userId, e);
            throw e;
        }
    }

    @Transactional
    public void cleanupExpiredSessions() {
        try {
            sessionRepository.cleanupExpiredSessions();
        } catch (Exception e) {
            log.error("Failed to cleanup expired sessions", e);
            throw e;
        }
    }

    private Map<String, Object> parseDeviceInfo(String userAgent) {
        Map<String, Object> deviceInfo = new HashMap<>();
        
        if (userAgent == null) {
            deviceInfo.put("type", "Unknown");
            deviceInfo.put("browser", "Unknown");
            deviceInfo.put("os", "Unknown");
            return deviceInfo;
        }
        
        String ua = userAgent.toLowerCase();
        
        // Определяем тип устройства
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
            deviceInfo.put("type", "Mobile");
        } else if (ua.contains("tablet")) {
            deviceInfo.put("type", "Tablet");
        } else {
            deviceInfo.put("type", "Desktop");
        }
        
        // Определяем браузер
        if (ua.contains("chrome")) {
            deviceInfo.put("browser", "Chrome");
        } else if (ua.contains("firefox")) {
            deviceInfo.put("browser", "Firefox");
        } else if (ua.contains("safari")) {
            deviceInfo.put("browser", "Safari");
        } else if (ua.contains("edge")) {
            deviceInfo.put("browser", "Edge");
        } else {
            deviceInfo.put("browser", "Other");
        }
        
        // Определяем ОС
        if (ua.contains("windows")) {
            deviceInfo.put("os", "Windows");
        } else if (ua.contains("mac")) {
            deviceInfo.put("os", "macOS");
        } else if (ua.contains("linux")) {
            deviceInfo.put("os", "Linux");
        } else if (ua.contains("android")) {
            deviceInfo.put("os", "Android");
        } else if (ua.contains("ios")) {
            deviceInfo.put("os", "iOS");
        } else {
            deviceInfo.put("os", "Other");
        }
        
        return deviceInfo;
    }
}
