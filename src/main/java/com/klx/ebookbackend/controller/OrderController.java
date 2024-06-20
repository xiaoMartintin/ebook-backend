package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @PostMapping
    public Map<String, Object> placeOrder(@RequestBody Map<String, Object> orderInfo) {
        return Collections.singletonMap("status", "success");
    }

    @GetMapping
    public List<Map<String, Object>> getOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        Map<String, Object> order = new HashMap<>();
        order.put("id", 1);
        order.put("user_id", 1);
        order.put("time", new Date());
        order.put("total_price", 10.99);
        order.put("receiver", "John Doe");
        order.put("address", "123 Main St");
        order.put("phone", "123-456-7890");

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("book_id", 1);
        item.put("title", "Sample Book");
        item.put("quantity", 1);
        item.put("price", 30.0);
        items.add(item);

        order.put("items", items);
        orders.add(order);
        return orders;
    }
}
