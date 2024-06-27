package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartService {
//    List<Cart> getCartItems(Integer userId);
    Page<Cart> getCartItems(Integer userId, Pageable pageable);
    void deleteCartItem(Integer id);
    void addCartItem(Integer userId, Integer bookId, int quantity);
    void changeCartItemQuantity(Integer id, int number);
    Optional<Cart> getCartById(Integer id);
}
