package com.klx.ebookbackend.utils;

import org.springframework.stereotype.Component;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/websocket/transfer/{userId}")
@Component
public class WebSocketServer {

    public WebSocketServer() {
        //每当有一个连接，都会执行一次构造方法
        System.out.println("新的连接。。。");
    }

    private static final AtomicInteger COUNT = new AtomicInteger();

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void sendMessage(Session toSession, String message) {
        if (toSession != null) {
            try {
                toSession.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("对方不在线");
        }
    }


    public void sendMessageToUser(String user, String message) {
        System.out.println(user);
        Session toSession = SESSIONS.get(user);
        sendMessage(toSession, message);
//        System.out.println(message);
    }

    //上面这两个函数可以有一个重试机制。在这个版本中，sendMessageToUser 会进行多次尝试，确保消息能够在用户上线后成功发送。工作流程如下：
    //方法每隔 1 秒（即 Thread.sleep(1000);）检查用户是否上线。
    //如果用户上线且消息发送成功（sendMessage 返回 0），则退出循环。
    //如果用户不在线，方法会重试最多 10 次，给用户上线的时间。
//    public int sendMessage(Session toSession, String message) {
//        if (toSession != null) {
//            try {
//                toSession.getBasicRemote().sendText(message);
//                return 0;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("对方不在线");
//            return 1;
//        }
//        return 1;
//    }
//    public void sendMessageToUser(String user, String message) throws InterruptedException {
//        System.out.println("向用户发送消息: " + user);
//        for (int i = 0; i < 10; i++) {
//            Session toSession = SESSIONS.get(user);
//            if (sendMessage(toSession, message) == 0)
//                return;  // 如果消息发送成功，则退出重试
//            Thread.sleep(1000);  // 每秒重试一次，最多重试10次
//        }
//    }


    @OnMessage
    public void onMessage(String message) {
        System.out.println("服务器收到消息：" + message);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        if (SESSIONS.get(userId) != null) {
            System.out.println("已经上线过了");
            return;
        }
        SESSIONS.put(userId.trim(), session);
        COUNT.incrementAndGet();
        System.out.println(userId + "上线了，当前在线人数：" + COUNT);

    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        SESSIONS.remove(userId);
        COUNT.decrementAndGet();
        System.out.println(userId + "下线了，当前在线人数：" + COUNT);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }
}



