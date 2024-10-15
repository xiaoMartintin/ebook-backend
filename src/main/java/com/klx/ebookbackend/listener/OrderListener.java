package com.klx.ebookbackend.listener;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.klx.ebookbackend.controller.WebSocketController;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.util.*;

@Component
public class OrderListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WebSocketController webSocketController;


    private ObjectMapper objectMapper;

    public OrderListener() {
        this.objectMapper = new ObjectMapper();
        // 注册 JavaTimeModule 以支持 Java 8 日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用写入日期时间为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void processOrderMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> orderData = objectMapper.readValue(message, Map.class);

            Integer userId = (Integer) orderData.get("userId");
            String address = (String) orderData.get("address");
            String receiver = (String) orderData.get("receiver");
            String tel = (String) orderData.get("tel");
            List<Integer> itemIds = (List<Integer>) orderData.get("itemIds");

            // 调用 OrderService 处理订单
            Order order = orderService.processOrder(userId, address, receiver, tel, itemIds);
            System.out.println("Order successfully processed for user: " + userId);

            // 将处理结果发送到前端
            response.put("message", "Order for user " + userId + " processed successfully");
            response.put("ok", true);
            response.put("data", order);

            // 使用 WebSocketController 通过 WebSocket 向前端发送订单处理结果
            webSocketController.sendOrderUpdate(userId.toString(), objectMapper.writeValueAsString(response));
            System.out.println("Order result message sent to WebSocket: " + response);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // 捕获 JSON 解析异常并处理
            response.put("message", "JSON parsing error: " + e.getMessage());
            response.put("ok", false);
            response.put("data", null);
            webSocketController.sendOrderUpdate("error", response.toString());
            System.out.println("Failed to process order due to JSON parsing error: " + e.getMessage());
        } catch (RuntimeException e) {
            // 捕获自定义异常（如余额不足）
            response.put("message", e.getMessage());
            response.put("ok", false);
            response.put("data", null);
            webSocketController.sendOrderUpdate("error", response.toString());
            System.out.println("Failed to process order: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常处理
            response.put("message", "Failed to process order message");
            response.put("ok", false);
            response.put("data", null);
            webSocketController.sendOrderUpdate("error", response.toString());
            System.out.println("Failed to process order message");
            e.printStackTrace();
        }
    }

}