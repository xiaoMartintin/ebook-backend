package com.klx.ebookbackend.serviceImpl;

import com.klx.ebookbackend.dao.CartDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartDao cartDao;

    @Override
    public Optional<Cart> getCartById(Integer id){
        return cartDao.getCartById(id);
    }

    @Override
    public List<Cart> getCartItems(Integer userId) {
        List<Cart> cartItems = cartDao.getCartItems(userId);
        // 确保每个Cart对象都有一个非空的Book对象
        for (Cart cart : cartItems) {
            if (cart.getBook() == null) {
                throw new IllegalStateException("Cart item is missing book information");
            }
        }
        return cartItems;
    }

    @Override
    public void deleteCartItem(Integer id) {
        cartDao.deleteCartItem(id);
    }

    @Override
    public void addCartItem(Integer userId, Integer bookId, int quantity) {
        User user = new User();
        user.setId(userId);

        Book book = new Book();
        book.setId(bookId);

        cartDao.addCartItem(user, book, quantity);
    }

    @Override
    public void changeCartItemQuantity(Integer id, int number) {
        cartDao.changeCartItemNumber(id, number);
    }
}
