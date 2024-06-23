package com.klx.ebookbackend.dao;

import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.entity.Book;

import java.util.List;
import java.util.Optional;

public interface CartDao {
    List<Cart> getAllCarts();
    Optional<Cart> getCartById(int id);
    Cart saveCart(Cart cart);
    void deleteCartItem(int id);
    List<Cart> getCartItems(Integer userId);
    void addCartItem(User user, Book book, int quantity);
    void changeCartItemNumber(int id, int number);
}
