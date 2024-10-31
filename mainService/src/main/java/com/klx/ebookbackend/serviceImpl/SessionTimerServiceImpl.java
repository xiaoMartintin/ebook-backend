package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.service.SessionTimerService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)// 设置为 Session 作用域
public class SessionTimerServiceImpl implements SessionTimerService {

    private long startTime; // 登录时的时间戳
    private long endTime;   // 登出时的时间戳

    // 开始计时
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
    }

    // 停止计时并返回持续时间
    public long stopTimer() {
        this.endTime = System.currentTimeMillis();
        return this.endTime - this.startTime;
    }
}
