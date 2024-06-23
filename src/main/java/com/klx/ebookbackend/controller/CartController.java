package com.klx.ebookbackend.controller;

import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<?> getUserItems(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
        }
        List<Cart> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(createResponse("Cart items retrieved", true, cartItems));
    }

    @PutMapping
    public ResponseEntity<?> addCartItem(@RequestParam Integer bookId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse("Unauthorized", false, null));
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
