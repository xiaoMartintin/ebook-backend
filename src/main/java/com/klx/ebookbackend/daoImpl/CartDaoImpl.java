package com.klx.ebookbackend.daoImpl;

import com.klx.ebookbackend.dao.CartDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.repository.CartRepository;
import com.klx.ebookbackend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CartDaoImpl implements CartDao {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

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
    public List<Cart> getCartItems(Integer userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        // 确保每个Cart对象都有一个非空的Book对象
        for (Cart cart : cartItems) {
            cart.setBook(bookRepository.findById(cart.getBook().getId()).orElse(null));
            if (cart.getBook() == null) {
                throw new IllegalStateException("Cart item is missing book information");
            }
        }
        return cartItems != null ? cartItems : Collections.emptyList();
    }



    @Override
    public void addCartItem(User user, Book book, int quantity) {
        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        Optional<Cart> existingCartItem = cartItems.stream()
                .filter(cart -> cart.getBook().getId() == book.getId())
                .findFirst();

        //有的话就加上去
        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            cartRepository.save(cart);
        } else {
            //没有再新建
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
