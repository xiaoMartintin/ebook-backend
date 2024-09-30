package com.klx.ebookbackend.serviceImpl;
import com.klx.ebookbackend.dao.OrderDao;
import com.klx.ebookbackend.dao.OrderItemDao;
import com.klx.ebookbackend.dao.BookDao;
import com.klx.ebookbackend.entity.Book;
import com.klx.ebookbackend.entity.Order;
import com.klx.ebookbackend.entity.OrderItem;
import com.klx.ebookbackend.entity.Cart;
import com.klx.ebookbackend.entity.User;
import com.klx.ebookbackend.service.OrderService;
import com.klx.ebookbackend.service.CartService;
import com.klx.ebookbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    @Override
    public Order placeOrder(Order order) {
        return orderDao.saveOrder(order);
    }

    /**
     * 处理订单的创建、校验、保存逻辑。
     */
    @Override
    @Transactional // 添加事务管理注解
    public Order processOrder(Integer userId, String address, String receiver, String tel, List<Integer> itemIds) {
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setReceiver(receiver);
        order.setTel(tel);
        order.setTime(Instant.now());

        Set<OrderItem> orderItems = new LinkedHashSet<>();
        double totalOrderPrice = 0.0;

        for (Integer itemId : itemIds) {
            Optional<Cart> cartItem = cartService.getCartById(itemId);

            if (cartItem.isPresent()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setBook(cartItem.get().getBook());
                orderItem.setQuantity(cartItem.get().getQuantity());
                totalOrderPrice += cartItem.get().getQuantity() * cartItem.get().getBook().getPrice();
                orderItems.add(orderItem);
            } else {
                throw new RuntimeException("Cart item not found for ID: " + itemId);
            }
        }

        if (user.getBalance() < totalOrderPrice) {
            throw new RuntimeException("Insufficient balance. Your current balance is: " + user.getBalance() +
                    ", but total order price is: " + totalOrderPrice);
        }

        // 扣除用户余额并设置订单信息
        user.setBalance(user.getBalance() - totalOrderPrice);
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalOrderPrice);

        // 保存订单
        return placeOrder(order);
    }


    @Override
    public Page<Order> getOrders(Integer userId, String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<Order> fetchedOrders = orderDao.findOrders(userId, keyword, startInstant, endInstant, pageRequest);
        System.out.println("fetchedOrders: " + fetchedOrders);

        fetchedOrders.forEach(order -> {
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
            if (orderItems != null) {
                orderItems.forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
                order.setOrderItems(new LinkedHashSet<>(orderItems));
            }
        });
        return fetchedOrders;
    }

    @Override
    public Page<Order> getAllOrders(String keyword, LocalDate startDate, LocalDate endDate, PageRequest pageRequest) {
        Instant startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        Page<Order> fetchedOrders = orderDao.findAllOrders(keyword, startInstant, endInstant, pageRequest);

        System.out.println("fetchedOrders: " + fetchedOrders);
        fetchedOrders.forEach(order -> {
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrder(order);
            if (orderItems != null) {
                orderItems.forEach(orderItem -> {
                    if (orderItem.getBook() != null) {
                        Book book = bookDao.getBookById(orderItem.getBook().getId());
                        orderItem.setBook(book);
                    }
                });
                order.setOrderItems(new LinkedHashSet<>(orderItems));
            }
        });
        return fetchedOrders;
    }

}
