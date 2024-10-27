package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.dto.PagedBooks;
import com.klx.ebookbackend.service.BookService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public ResponseEntity<?> searchBooks(@RequestParam String keyword, @RequestParam int pageIndex, @RequestParam int pageSize, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("Session ID (getBooks): " + session.getId());
        System.out.println("User ID in session (getBooks): " + userId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<Book> books = bookService.searchBooks(keyword, pageIndex, pageSize);
        int total = bookService.getTotalBooksCount(keyword);
        PagedBooks pagedBooks = new PagedBooks(books, total);
        return ResponseEntity.ok(pagedBooks);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<?> getBookById(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("Session ID (getBookById): " + session.getId());
        System.out.println("User ID in session (getBookById): " + userId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Book book = bookService.getBookById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/books/rank")
    public ResponseEntity<?> getTop10BestSellingBooks(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("Session ID (getTop10BestSellingBooks): " + session.getId());
        System.out.println("User ID in session (getTop10BestSellingBooks): " + userId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<Book> books = bookService.getTop10BestSellingBooks();
        return ResponseEntity.ok(books);
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody Book book, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody Book book, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        book.setId(id);
        Book updatedBook = bookService.saveBook(book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null || !userService.isUserAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
}
