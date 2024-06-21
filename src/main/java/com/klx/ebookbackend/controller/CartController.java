package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.service.BookService;
import com.klx.ebookbackend.service.BookService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final BookController bookController;
    private final BookService bookService;

    public CartController(BookController bookController, BookService bookService) {
        this.bookController = bookController;
        this.bookService = bookService;
    }

    @GetMapping
    public List<Map<String, Object>> getCartItems() {
        List<Map<String, Object>> cartItems = new ArrayList<>();

        // 示例购物车项目
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("number", 1);

        // 获取书籍详细信息
        Book book1 = bookService.getBookById(1);
        item.put("book", book1);

        cartItems.add(item);
        return cartItems;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCartItem(@PathVariable String id) {
        return Collections.singletonMap("status", "success");
    }

    @PutMapping
    public Map<String, Object> addCartItem(@RequestParam int bookId, @RequestParam int number) {
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("id", UUID.randomUUID().toString());
        newItem.put("number", number);

        // 获取书籍详细信息
        Book book2 = bookService.getBookById(bookId);
        newItem.put("book", book2);

        return Collections.singletonMap("status", "success");
    }

    @PutMapping("/{id}")
    public Map<String, Object> changeCartItemNumber(@PathVariable String id, @RequestParam int number) {
        return Collections.singletonMap("status", "success");
    }
}
