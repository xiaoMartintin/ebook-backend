package com.klx.ebookbackend.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        // Allow requests to /register and /login without session validation
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.debug("Session ID: {}", session.getId());
            logger.debug("User ID in session: {}", session.getAttribute("userId"));
            if (session.getAttribute("userId") != null) {
                logger.debug("Authorized request to: {}", requestURI);
                return true;
            } else {
                logger.warn("No user ID in session for request to: {}", requestURI);
            }
        } else {
            logger.warn("No session found for request to: {}", requestURI);
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
