package com.klx.ebookbackend.utils;

import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtils {
    public static HttpSession getSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession session = servletRequestAttributes.getRequest().getSession();
        return session;
    }
}


