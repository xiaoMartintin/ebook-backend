package com.klx.ebookbackend.config;

import com.klx.ebookbackend.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SessionConfig implements WebMvcConfigurer {
    @Autowired
    private SessionInterceptor sessionInterceptor;

    //addInterceptors 方法将 SessionInterceptor 添加到拦截器链中，排除了 /api/login 和 /api/register 两个路径。
    //addCorsMappings 方法允许跨域请求，允许的来源是 http://localhost:3000。
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/register");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("http://localhost:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}

//