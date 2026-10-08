package com.example.smartexpensetracker.config;

import com.example.smartexpensetracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that guards API endpoints against unauthenticated access.
 * Checks for the presence of an active HTTP session containing the authenticated USER_ID.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_USER_ID = "USER_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Allow CORS pre-flight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            throw new UnauthorizedException("Authentication required. Please log in to continue.");
        }

        return true;
    }
}
