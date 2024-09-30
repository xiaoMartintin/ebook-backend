package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import com.klx.ebookbackend.dto.OrderInfo;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

//    @PostMapping("/order")
//    // 这是原来的同步下订单
//    public ResponseEntity<?> placeOrderSync(@RequestBody OrderInfo orderInfo, HttpSession session) {
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
//        }
//
//        // Debugging output
//        System.out.println("OrderInfo received: " + orderInfo);
//
//        String address = orderInfo.getAddress();
//        String receiver = orderInfo.getReceiver();
//        String tel = orderInfo.getTel();
//        List<Integer> itemIds = orderInfo.getItemIds();
//
//        // 校验订单信息是否完整
//        if (address == null || receiver == null || tel == null || itemIds == null || itemIds.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Invalid order info", false, null));
//        }
//
//        try {
//            // 调用 OrderService 处理订单
//            Order order = orderService.processOrder(userId, address, receiver, tel, itemIds);
//            return ResponseEntity.ok(createResponse("Order successfully placed", true, null));
//        } catch (RuntimeException e) {
//            // 捕获自定义的余额不足异常
//            if (e.getMessage().contains("Insufficient balance")) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Insufficient balance", false, null));
//            }
//            // 捕获其他运行时异常并返回 500 错误码
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("Error processing order: " + e.getMessage(), false, null));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse("Error placing order", false, null));
//        }
//    }

    @PostMapping("/order")
    public ResponseEntity<?> sendOrderToKafka(@RequestBody OrderInfo orderInfo, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("User not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        // 校验订单信息是否完整
        String address = orderInfo.getAddress();
        String receiver = orderInfo.getReceiver();
        String tel = orderInfo.getTel();
        List<Integer> itemIds = orderInfo.getItemIds();
        if (address == null || receiver == null || tel == null || itemIds == null || itemIds.isEmpty()) {
            System.out.println("Invalid order info received: " + orderInfo);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Invalid order info", false, null));
        }

        // 创建订单消息对象
        Map<String, Object> orderMessage = new HashMap<>();
        orderMessage.put("userId", userId);
        orderMessage.put("address", address);
        orderMessage.put("receiver", receiver);
        orderMessage.put("tel", tel);
        orderMessage.put("itemIds", itemIds);

        try {
            // 将订单消息发送到 Kafka 的 "order-topic"
            kafkaTemplate.send("order-topic", userId.toString(), objectMapper.writeValueAsString(orderMessage));
            System.out.println("Order message successfully sent to Kafka: " + orderMessage);
        } catch (Exception e) {
            System.out.println("Error sending order message to Kafka: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createResponse("Failed to send order message to Kafka", false, null));
        }

        System.out.println("Order sent to Kafka for user ID: " + userId + " with order details: " + orderMessage);
        return ResponseEntity.ok(createResponse("Order message successfully sent to Kafka", true, null));
    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        System.out.println("keyword: " + keyword);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        Page<Order> orderPage = orderService.getOrders(userId, keyword, startDate, endDate, PageRequest.of(pageIndex, pageSize));
        System.out.println(orderPage.getContent());

        if (orderPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createResponse("No orders found", true, orderPage.getContent()));
        }

        return ResponseEntity.ok(createResponse("Orders retrieved", true, orderPage));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        System.out.println("Keyword: " + keyword);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createResponse("Access denied", false, null));
        }

        Page<Order> orderPage = orderService.getAllOrders(keyword, startDate, endDate, PageRequest.of(pageIndex, pageSize));

        System.out.println("Orders: " + orderPage.getContent());

        if (orderPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(createResponse("No orders found", true, orderPage.getContent()));
        }

        return ResponseEntity.ok(createResponse("Orders retrieved", true, orderPage));
    }






    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }


}
