package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.CartDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CartDaoImpl implements CartDao {

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private BookDao bookDao;

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<Cart> getCartById(int id) {
        return cartRepository.findById(id);
    }

    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public void deleteCartItem(int id) {
        cartRepository.deleteById(id);
    }

    @Override
    public Page<Cart> getCartItems(Integer userId, Pageable pageable) {
        Page<Cart> cartItems = cartRepository.findByUserId(userId, pageable);
        // 确保每个Cart对象都有一个非空的Book对象
        for (Cart cart : cartItems) {
            // 使用 BookDao 获取书籍信息
            cart.setBook(bookDao.getBookById(cart.getBook().getId()));
            if (cart.getBook() == null) {
                throw new IllegalStateException("Cart item is missing book information");
            }
        }
        return cartItems;
    }




    @Override
    public void addCartItem(User user, Book book, int quantity) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // 使用一个足够大的页码来确保获取所有项目
        Page<Cart> cartItemsPage = cartRepository.findByUserId(user.getId(), pageable);
        List<Cart> cartItems = cartItemsPage.getContent();

        Optional<Cart> existingCartItem = cartItems.stream()
                .filter(cart -> cart.getBook().getId() == book.getId())
                .findFirst();

        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setBook(book);
            cart.setQuantity(quantity);
            cartRepository.save(cart);
        }
    }


    @Override
    public void changeCartItemNumber(int id, int number) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cart.setQuantity(number);
        cartRepository.save(cart);
    }
}
