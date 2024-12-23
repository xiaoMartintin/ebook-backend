package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.service.BookService;
import com.klx.ebookbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final BookService bookService;

    @Autowired
    public CartController(CartService cartService, BookService bookService) {
        this.cartService = cartService;
        this.bookService = bookService;
    }

    @GetMapping()
    public ResponseEntity<?> getUserItemsPaged(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
        }
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<Cart> cartItems = cartService.getCartItems(userId, pageable);
        return ResponseEntity.ok(createResponse("Paged cart items retrieved", true, cartItems));
    }

    @PutMapping
    public ResponseEntity<?> addCartItem(@RequestParam Integer bookId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
        }

        Book book = bookService.getBookById(bookId);
        if (book == null || book.getInventory() < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Not enough inventory", false, null));
        }

        int quantity = 1; // 默认数量为 1
        cartService.addCartItem(userId, bookId, quantity);
        return ResponseEntity.ok(createResponse("Item added to cart", true, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Integer id, @RequestParam int quantity, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
        }

        Optional<Cart> cartItemOptional = cartService.getCartById(id);
        if (!cartItemOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Cart item not found", false, null));
        }

        Cart cartItem = cartItemOptional.get();
        Book book = bookService.getBookById(cartItem.getBook().getId());
        if (book == null || book.getInventory() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse("Not enough inventory", false, null));
        }

        cartService.changeCartItemQuantity(id, quantity);
        return ResponseEntity.ok(createResponse("Cart item quantity updated", true, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
        }

        cartService.deleteCartItem(id);
        return ResponseEntity.ok(createResponse("Cart item deleted", true, null));
    }

    private Map<String, Object> createResponse(String message, boolean ok, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("ok", ok);
        response.put("data", data);
        return response;
    }
}
