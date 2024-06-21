package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<PagedBooks> searchBooks(@RequestParam String keyword, @RequestParam int pageIndex, @RequestParam int pageSize) {
        List<Book> books = bookService.searchBooks(keyword, pageIndex, pageSize);
        int total = bookService.getTotalBooksCount(keyword);
        PagedBooks pagedBooks = new PagedBooks(books, total);
        return ResponseEntity.ok(pagedBooks);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/books/rank")
    public ResponseEntity<List<Book>> getTop10BestSellingBooks() {
        List<Book> books = bookService.getTop10BestSellingBooks();
        return ResponseEntity.ok(books);
    }

    public static class PagedBooks {
        private List<Book> items;
        private int total;

        public PagedBooks(List<Book> items, int total) {
            this.items = items;
            this.total = total;
        }

        public List<Book> getItems() {
            return items;
        }

        public void setItems(List<Book> items) {
            this.items = items;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
