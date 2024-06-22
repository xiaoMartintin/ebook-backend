package com.klx.ebookbackend.service;

import com.klx.ebookbackend.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getCartItems(Integer userId);
    void deleteCartItem(Integer id);
    void addCartItem(Integer userId, Integer bookId, int quantity);
    void changeCartItemQuantity(Integer id, int number);
}
