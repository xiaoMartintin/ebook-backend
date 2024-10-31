package com.klx.microservice.controller;

import com.klx.microservice.entity.Book;
import com.klx.microservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Gateway里面已经把microservice去掉了，所以这里就不用在mapping到microservice了
//@RequestMapping("/microservice")
@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // 模糊查找书籍名称并返回对应的作者信息
    @GetMapping("/getBookAuthorByName/{bookName}")
    public ResponseEntity<List<Map<String, String>>> getBookAuthorByName(@PathVariable("bookName") String bookName) {
        List<Book> books = bookRepository.findByTitleContaining(bookName);
        List<Map<String, String>> results = new ArrayList<>();

        for (Book book : books) {
            Map<String, String> bookInfo = new HashMap<>();
            bookInfo.put("title", book.getTitle());
            bookInfo.put("author", book.getAuthor());
            results.add(bookInfo);
        }
        return ResponseEntity.ok(results);
    }


}
