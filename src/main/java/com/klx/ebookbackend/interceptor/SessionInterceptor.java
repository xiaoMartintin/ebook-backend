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
//SessionInterceptor 类实现了 HandlerInterceptor 接口，用于在每个请求处理前进行会话验证。

    private static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);

    //preHandle 方法在每个请求处理前执行。如果是 OPTIONS 请求，直接返回 true。
    //如果请求路径是 /api/user/register 或 /api/user/login，也直接返回 true，不进行会话验证。
    //通过 request.getSession(false) 获取现有会话（如果不存在，则返回 null），并检查会话中是否包含 userId 属性，如果存在则表示用户已登录。
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
