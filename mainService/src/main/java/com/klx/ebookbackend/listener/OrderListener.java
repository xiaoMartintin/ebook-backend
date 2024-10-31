package com.klx.ebookbackend.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.utils.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
public class OrderListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WebSocketServer webSocketServer;

    private ObjectMapper objectMapper;

    public OrderListener() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void processOrderMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> orderData = objectMapper.readValue(message, Map.class);
            Integer userId = (Integer) orderData.get("userId");

            Order order = orderService.processOrder(userId, (String) orderData.get("address"),
                    (String) orderData.get("receiver"),
                    (String) orderData.get("tel"),
                    (List<Integer>) orderData.get("itemIds"));
            response.put("message", "订单处理成功");
            response.put("ok", true);
            response.put("data", order);

            // 向指定用户推送消息
            webSocketServer.sendMessageToUser(userId.toString(), objectMapper.writeValueAsString(response));
            System.out.println("订单处理结果已发送至用户WebSocket");

        } catch (Exception e) {
            System.out.println("处理订单时出错：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
