package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final BookController bookController;

    public CartController(BookController bookController) {
        this.bookController = bookController;
    }

    @GetMapping
    public List<Map<String, Object>> getCartItems() {
        List<Map<String, Object>> cartItems = new ArrayList<>();

        // 示例购物车项目
        Map<String, Object> item = new HashMap<>();
        item.put("id", "1");
        item.put("userId", "1");
        item.put("quantity", 1);

        // 获取书籍详细信息
        Map<String, Object> bookDetails = bookController.getBookById(1);
        item.put("book", bookDetails);  // 将书籍详细信息放入购物车项中

        cartItems.add(item);
        return cartItems;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCartItem(@PathVariable String id) {
        return Collections.singletonMap("status", "success");
    }

    @PutMapping
    public Map<String, Object> addCartItem(@RequestParam int bookId, @RequestParam int quantity, @RequestParam String userId) {
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("id", UUID.randomUUID().toString());
        newItem.put("userId", userId);
        newItem.put("quantity", quantity);

        // 获取书籍详细信息
        Map<String, Object> bookDetails = bookController.getBookById(bookId);
        newItem.put("book", bookDetails);  // 将书籍详细信息放入购物车项中

        return Collections.singletonMap("status", "success");
    }

    @PutMapping("/{id}")
    public Map<String, Object> changeCartItemNumber(@PathVariable String id, @RequestParam int number) {
        return Collections.singletonMap("status", "success");
    }
}
