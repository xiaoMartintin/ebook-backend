package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final BookController bookController;

    public OrderController(BookController bookController) {
        this.bookController = bookController;
    }

    @PostMapping
    public Map<String, Object> placeOrder(@RequestBody Map<String, Object> orderInfo) {
        // 打印订单信息到Tomcat Console
        System.out.println("Order received: " + orderInfo);

        // 提取并验证订单信息
        Integer userId = (Integer) orderInfo.get("userId");
        String address = (String) orderInfo.get("address");
        String receiver = (String) orderInfo.get("receiver");
        String tel = (String) orderInfo.get("tel");
        List<Integer> itemIds = (List<Integer>) orderInfo.get("itemIds");

        // 检查orderInfo中的必需字段是否为空
        if (userId == null || address == null || receiver == null || tel == null || itemIds == null) {
            return Collections.singletonMap("status", "failure");
        }

        // 创建订单对象
        Map<String, Object> order = new HashMap<>();
        order.put("id", UUID.randomUUID().toString());
        order.put("user_id", userId);
        order.put("address", address);
        order.put("receiver", receiver);
        order.put("tel", tel);
        order.put("createdAt", new Date());

        // 添加订单项
        List<Map<String, Object>> items = new ArrayList<>();
        for (int itemId : itemIds) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", itemId);
            Map<String, Object> bookDetails = bookController.getBookById(itemId);
            // 检查bookDetails是否为空
            if (bookDetails != null) {
                item.put("book", bookDetails);
                item.put("number", 1);  // 假设每个订单项的数量为1
                items.add(item);
            }
        }
        order.put("items", items);

        // 模拟保存订单到数据库或其他存储
        // 例如，orderService.save(order);

        // 返回确认消息
        return Collections.singletonMap("status", "success");
    }

    @GetMapping
    public List<Map<String, Object>> getOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        Map<String, Object> order = new HashMap<>();
        order.put("id", 1);
        order.put("receiver", "John Doe");
        order.put("address", "123 Main St");
        order.put("tel", "123-456-7890");
        order.put("createdAt", new Date().toString());

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("book", Collections.singletonMap("title", "Sample Book"));
        item.put("number", 1);
        items.add(item);

        order.put("items", items);
        orders.add(order);
        return orders;
    }
}
