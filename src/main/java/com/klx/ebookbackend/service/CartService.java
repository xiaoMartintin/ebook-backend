package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Cart;

import java.util.List;
import java.util.Optional;

public interface CartService {
    List<Cart> getCartItems(Integer userId);
    void deleteCartItem(Integer id);
    void addCartItem(Integer userId, Integer bookId, int quantity);
    void changeCartItemQuantity(Integer id, int number);
    Optional<Cart> getCartById(Integer id);
}
