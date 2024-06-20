package com.klx.ebookbackend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class BookController {

    private List<Map<String, Object>> books = new ArrayList<>();

    public BookController() {
        // 初始化一些书籍数据
        addBook(1, "9787573604309", "狂飙 原著", "徐纪周朱俊懿", 30.0, "《狂飙 原著》由徐纪周朱俊懿创作，真实还原扫黑除恶第一线，以横跨20年的群像叙事方式全景式地展现时代变迁下的黑白较量与复杂人性。", 998, "http://img3m0.ddimg.cn/28/9/11359708300-1_b_1.jpg", 5);
        addBook(2, "9787517825692", "泰戈尔诗集", "泰戈尔", 8.89, "《泰戈尔诗集》是印度著名诗人泰戈尔的代表作，包含《新月集》和《飞鸟集》，这些诗集因其深刻的哲理和美妙的语言获得了诺贝尔文学奖。", 998, "http://img3m5.ddimg.cn/57/13/26514435-1_b_11.jpg", 3);
        addBook(3, "9787111213826", "Java编程思想", "Bruce Eckel", 91.2, "《Java编程思想》是Bruce Eckel的经典著作，是每个Java程序员的必读书籍，详细讲解了Java的核心概念和高级编程技巧。", 9095, "http://img3m0.ddimg.cn/4/24/9317290-1_w_5.jpg", 8);
    }

    private void addBook(int id, String isbn, String title, String author, double price, String description, int inventory, String cover, int sales) {
        Map<String, Object> book = new HashMap<>();
        book.put("id", id);
        book.put("isbn", isbn);
        book.put("title", title);
        book.put("author", author);
        book.put("price", price);
        book.put("description", description);
        book.put("inventory", inventory);
        book.put("cover", cover);
        book.put("sales", sales);
        books.add(book);
    }

    @GetMapping("/books")
    public Map<String, Object> searchBooks(@RequestParam String keyword, @RequestParam int pageIndex, @RequestParam int pageSize) {
        List<Map<String, Object>> filteredBooks = new ArrayList<>();
        if (keyword == null) {
            keyword = "";
        }
        for (Map<String, Object> book : books) {
            //因为关键字为空，所以contain总为真，所以会返回所有的书
            if ((book.get("title") != null && book.get("title").toString().toLowerCase().contains(keyword.toLowerCase())) ||
                    (book.get("author") != null && book.get("author").toString().toLowerCase().contains(keyword.toLowerCase()))) {
                filteredBooks.add(book);
            }
        }

        //分页
        int start = Math.max((pageIndex - 1) * pageSize, 0);  // 修正起始索引，确保不为负数
        int end = Math.min(start + pageSize, filteredBooks.size());

        List<Map<String, Object>> paginatedBooks = filteredBooks.subList(start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("total", filteredBooks.size());
        response.put("items", paginatedBooks);
        return response;
    }

    @GetMapping("/book/{id}")
    public Map<String, Object> getBookById(@PathVariable int id) {
        for (Map<String, Object> book : books) {
            if (book.get("id").equals(id)) {
                return book;
            }
        }
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Book not found");
        return errorResponse;
    }

    @GetMapping("/books/rank")
    public List<Map<String, Object>> getTop10BestSellingBooks() {
        List<Map<String, Object>> topSellingBooks = new ArrayList<>(books);
        topSellingBooks.sort((b1, b2) -> Integer.compare((int) b2.get("sales"), (int) b1.get("sales")));
        return topSellingBooks.subList(0, Math.min(10, topSellingBooks.size()));
    }
}
