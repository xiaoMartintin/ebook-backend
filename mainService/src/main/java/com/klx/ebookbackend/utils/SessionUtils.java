package com.klx.ebookbackend.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//getSession 方法通过 RequestContextHolder 获取当前请求的 ServletRequestAttributes，并返回当前请求的会话。

public class SessionUtils {
    public static HttpSession getSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession session = servletRequestAttributes.getRequest().getSession();
        return session;
    }
}


