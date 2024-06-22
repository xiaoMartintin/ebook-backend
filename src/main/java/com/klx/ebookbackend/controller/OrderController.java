package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.UserService;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderInfo orderInfo, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        String address = orderInfo.getAddress();
        String receiver = orderInfo.getReceiver();
        String tel = orderInfo.getTel();
        List<Integer> itemIds = orderInfo.getItemIds();

        if (address == null || receiver == null || tel == null || itemIds == null || itemIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Invalid order info", false, null));
        }

        Order order = new Order();
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setTel(tel);
        order.setTime(Instant.now());
        order.setUser(userService.getUserById(userId).orElse(null));

        Set<OrderItem> orderItems = new LinkedHashSet<>();
        for (Integer itemId : itemIds) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(bookService.getBookById(itemId));
            orderItem.setQuantity(1); // 假设每个商品数量为1，具体需求可调整
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        orderService.placeOrder(order);

        return ResponseEntity.ok(createResponse("Order successfully placed", true, null));
    }

    @GetMapping
    public ResponseEntity<?> getOrders(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("User not logged in", false, null));
        }

        Optional<Order> optionalOrder = orderService.getOrders(userId);
        List<Order> orders = optionalOrder.map(Collections::singletonList).orElse(Collections.emptyList());

        return ResponseEntity.ok(orders);
    }

    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }

    public static class OrderInfo {
        private String address;
        private String receiver;
        private String tel;
        private List<Integer> itemIds;

        // Getters and Setters

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public List<Integer> getItemIds() {
            return itemIds;
        }

        public void setItemIds(List<Integer> itemIds) {
            this.itemIds = itemIds;
        }
    }
}
